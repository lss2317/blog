package com.lss.strategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lss.common.Result;
import com.lss.config.WeiBoConfigProperties;
import com.lss.constant.LoginConst;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.User;
import com.lss.enums.UserEnum;
import com.lss.service.RedisService;
import com.lss.service.UserService;
import com.lss.strategy.WeiBoLoginStrategy;
import com.lss.utils.IpUtils;
import com.lss.utils.JWTUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lss
 * @create 2022年03月25日 18:53
 */
@Service
public class WeiBoLoginStrategyImpl implements WeiBoLoginStrategy {

    @Resource
    WeiBoConfigProperties weiboConfigProperties;
    @Resource
    RestTemplate restTemplate;
    @Resource
    UserService userService;
    @Resource
    HttpServletRequest request;
    @Resource
    RedisService redisService;

    @Override
    public Result<?> weiboLogin(JSONObject object) {
        //获取微博用户信息
        JSONObject LoginUserInfo = getWeiboLoginInfo(object);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("weibo_id", LoginUserInfo.get("id"));
        User one = userService.getOne(queryWrapper);
        //用户从未登录过
        if (one == null) {
            User user = new User();
            user.setWeiboId(LoginUserInfo.getString("id"));
            user.setNickname(LoginUserInfo.getString("screen_name"));
            user.setAvatar(LoginUserInfo.getString("avatar_hd"));
            user.setLoginType(3);
            Date date = new Date();
            user.setCreateTime(date);
            user.setLastLoginTime(date);
            String ipAddress = IpUtils.getIpAddress(request);
            user.setIpAddress(ipAddress);
            user.setIpSource(IpUtils.getIpSource(ipAddress));
            boolean save = userService.save(user);
            if (!save) {
                return Result.getUserResult(null, UserEnum.LOGIN_ERROR);
            }
            User saveUser = userService.getOne(new QueryWrapper<User>().eq("weibo_id", LoginUserInfo.getString("id")));
            String token = JWTUtils.createToken(saveUser.getId().toString(), "weibo");
            saveUser.setIpAddress(token);
            saveUser.setIpSource(token);
            return Result.getUserResult(saveUser, UserEnum.LOGIN_SUCCESS);
        }
        //查看是否被禁用
        if (one.getIsDisable() == 1) {
            return Result.getUserResult(null, UserEnum.DISABLE_LOGIN);
        }
        //数据库中有用户数据
        String token = JWTUtils.createToken(one.getId().toString(), one.getUsername());
        one.setIpAddress(token);
        one.setIpSource(token);
        //获取点赞说说列表
        Set<Object> set = redisService.sMembers(RedisPrefixConst.TALK_USER_LIKE + one.getId());
        one.setTalkLikeSet(set);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("last_login_time", new Date());
        updateWrapper.eq("weibo_id", LoginUserInfo.get("id"));
        userService.update(updateWrapper);
        return Result.getUserResult(one, UserEnum.LOGIN_SUCCESS);
    }

    /**
     * 调用微博第三方接口获取登录用户信息
     *
     * @param object 接收微博接口回调code
     * @return 用户json数据
     */
    private JSONObject getWeiboLoginInfo(JSONObject object) {
        //回调参数code
        String code = object.get("code").toString();
        MultiValueMap<String, String> codeData = new LinkedMultiValueMap<>();
        // 定义微博token请求参数
        codeData.add(LoginConst.CLIENT_ID, weiboConfigProperties.getAppId());
        codeData.add(LoginConst.CLIENT_SECRET, weiboConfigProperties.getAppSecret());
        codeData.add(LoginConst.GRANT_TYPE, weiboConfigProperties.getGrantType());
        codeData.add(LoginConst.REDIRECT_URI, weiboConfigProperties.getRedirectUrl());
        codeData.add(LoginConst.CODE, code);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(codeData, null);
        //调用接口获取微博access_token信息
        JSONObject tokenResult = restTemplate.exchange(weiboConfigProperties.getAccessTokenUrl(), HttpMethod.POST, requestEntity, JSONObject.class).getBody();
        // 定义请求参数
        Map<String, String> data = new HashMap<>(2);
        data.put(LoginConst.UID, tokenResult.get("uid").toString());
        data.put(LoginConst.ACCESS_TOKEN, tokenResult.get("access_token").toString());
        // 调用接口获取微博用户信息
        return JSONObject.parseObject(restTemplate.getForObject(weiboConfigProperties.getUserInfoUrl(), String.class, data), JSONObject.class);
    }


}
