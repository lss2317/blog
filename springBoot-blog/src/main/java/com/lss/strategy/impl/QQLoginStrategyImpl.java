package com.lss.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lss.common.Result;
import com.lss.config.QQConfigProperties;
import com.lss.constant.LoginConst;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.User;
import com.lss.enums.UserEnum;
import com.lss.service.RedisService;
import com.lss.service.UserService;
import com.lss.strategy.QQLoginStrategy;
import com.lss.utils.IpUtils;
import com.lss.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

/**
 * @author lss
 * @create 2022年03月07日 14:51
 */
@Service
public class QQLoginStrategyImpl implements QQLoginStrategy {

    @Autowired
    QQConfigProperties qqConfigProperties;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    UserService userService;
    @Resource
    HttpServletRequest request;
    @Resource
    RedisService redisService;


    @Override
    public Result<?> qqLogin(JSONObject jsonObject) {
        //获取openId
        String openId = (String) jsonObject.get("openId");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("qq_open_id", openId);
        User qqUser = userService.getOne(queryWrapper);
        //该QQ用户从未登录过
        if (qqUser == null) {
            //拿到accessToken
            String accessToken = (String) jsonObject.get("accessToken");
            JSONObject object = new JSONObject();
            object.put(LoginConst.QQ_OPEN_ID, openId);
            object.put(LoginConst.ACCESS_TOKEN, accessToken);
            object.put(LoginConst.OAUTH_CONSUMER_KEY, qqConfigProperties.getAppId());
            JSONObject json = JSON.parseObject(restTemplate.getForObject(qqConfigProperties.getUserInfoUrl(), String.class, object), JSONObject.class);
            User user = new User();
            user.setQqOpenId(openId);
            user.setNickname((String) json.get("nickname"));
            user.setAvatar((String) json.get("figureurl_2"));
            user.setLoginType(2);
            Date date = new Date();
            user.setCreateTime(date);
            user.setLastLoginTime(date);
            String ipAddress = IpUtils.getIpAddress(request);
            user.setIpAddress(ipAddress);
            user.setIpSource(IpUtils.getIpSource(ipAddress));
            boolean save = userService.save(user);
            if (!save) {
                return Result.getUserResult(null, UserEnum.STRATEGY_LOGIN_ERROR);
            }
            User saveUser = userService.getOne(new QueryWrapper<User>().eq("qq_open_id", openId));
            String token = JWTUtils.createToken(saveUser.getId().toString(), "qq");
            saveUser.setIpAddress(token);
            saveUser.setIpSource(token);
            return Result.getUserResult(saveUser, UserEnum.LOGIN_SUCCESS);
        }
        //查看是否被禁用
        if (qqUser.getIsDisable() == 1) {
            return Result.getUserResult(null, UserEnum.DISABLE_LOGIN);
        }
        String token = JWTUtils.createToken(qqUser.getId().toString(), "qq");
        qqUser.setIpAddress(token);
        qqUser.setIpSource(token);
        //获取点赞说说列表
        Set<Object> set = redisService.sMembers(RedisPrefixConst.TALK_USER_LIKE + qqUser.getId());
        qqUser.setTalkLikeSet(set);
        //获取点赞评论id的set集合
        Set<Object> setCommentLikes = redisService.sMembers(RedisPrefixConst.COMMENT_USER_LIKE + qqUser.getId());
        qqUser.setCommentLikeSet(setCommentLikes);
        //获取点赞文章id的set集合
        Set<Object> articleLikeSet = redisService.sMembers(RedisPrefixConst.ARTICLE_USER_LIKE + qqUser.getId());
        qqUser.setArticleLikeSet(articleLikeSet);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("last_login_time", new Date());
        updateWrapper.eq("qq_open_id", openId);
        userService.update(updateWrapper);
        return Result.getUserResult(qqUser, UserEnum.LOGIN_SUCCESS);
    }
}
