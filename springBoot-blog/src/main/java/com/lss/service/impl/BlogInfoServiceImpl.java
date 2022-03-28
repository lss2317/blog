package com.lss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.Article;
import com.lss.entity.Page;
import com.lss.entity.User;
import com.lss.entity.WebsiteConfig;
import com.lss.enums.FriendLinkEnum;
import com.lss.enums.UserEnum;
import com.lss.mapper.BlogInfoMapper;
import com.lss.service.*;
import com.lss.utils.IpUtils;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lss
 * @create 2022年03月17日 22:00
 */
@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, WebsiteConfig> implements BlogInfoService {

    @Resource
    RedisService redisService;
    @Resource
    HttpServletRequest request;
    @Resource
    UserService userService;
    @Resource
    ArticleService articleService;
    @Resource
    ClassificationService classificationService;
    @Resource
    TagService tagService;
    @Resource
    PageService pageService;


    @Override
    public String getAbout() {
        Object about = redisService.get(RedisPrefixConst.ABOUT);
        return Objects.nonNull(about) ? about.toString() : "";
    }

    @Override
    public Result<?> updateAbout(String aboutContent) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.NOT_ABILITY_UPDATE);
        }
        redisService.set(RedisPrefixConst.ABOUT, aboutContent);
        return Result.getUserResult(null, UserEnum.UPDATE_ROLE_SUCCESS);
    }

    @Override
    public Result<?> updateWebsiteConfig(WebsiteConfig websiteConfig) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.NOT_ABILITY_UPDATE);
        }
        // 修改网站配置
        UpdateWrapper<WebsiteConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("update_time", new Date());
        updateWrapper.set("config", JSON.toJSONString(websiteConfig));
        updateWrapper.eq("id", 1);
        this.update(updateWrapper);
        // 删除缓存
        redisService.del(RedisPrefixConst.WEBSITE_CONFIG);
        return Result.getUserResult(null, UserEnum.UPDATE_ROLE_SUCCESS);
    }

    @Override
    public WebsiteConfig getWebsiteConfig() {
        WebsiteConfig result;
        // 获取缓存数据
        Object websiteConfig = redisService.get(RedisPrefixConst.WEBSITE_CONFIG);
        if (Objects.nonNull(websiteConfig)) {
            result = JSON.parseObject(websiteConfig.toString(), WebsiteConfig.class);
        } else {
            // 从数据库中加载
            String config = this.getById(1).getConfig();
            result = JSON.parseObject(config, WebsiteConfig.class);
            redisService.set(RedisPrefixConst.WEBSITE_CONFIG, config);
        }
        return result;
    }

    @Override
    public JSONObject getBlogInfo() {
        //查询文章数量
        UpdateWrapper<Article> articleUpdateWrapper = new UpdateWrapper<>();
        articleUpdateWrapper.eq("is_delete", 0);
        articleUpdateWrapper.eq("status", 1);
        long articleCount = articleService.count(articleUpdateWrapper);
        // 查询分类数量
        long classCount = classificationService.count();
        //查询标签数量
        long tagCount = tagService.count();
        // 查询访问量
        Object count = redisService.get(RedisPrefixConst.BLOG_VIEWS_COUNT);
        String viewsCount = Optional.ofNullable(count).orElse(0).toString();
        // 查询网站配置
        WebsiteConfig websiteConfig = this.getWebsiteConfig();
        // 查询页面图片
        List<Page> pageList = pageService.listPages();
        JSONObject json = new JSONObject();
        json.put("websiteConfig", websiteConfig);
        json.put("articleCount", articleCount);
        json.put("classCount", classCount);
        json.put("tagCount", tagCount);
        json.put("viewsCount", viewsCount);
        json.put("pageList", pageList);
        return json;
    }

    @Override
    public void report() {
        // 获取ip
        String ipAddress = IpUtils.getIpAddress(request);
        // 访客唯一标识 ，前缀 + ip地址
        String uuid = RedisPrefixConst.UNIQUE_VISITOR + "_" + ipAddress;
        Object viewUser = redisService.get(uuid);
        // 一个ip地址访问12小时过后才会继续增加访问量
        if (viewUser == null) {
            // 访问量+1
            redisService.incr(RedisPrefixConst.BLOG_VIEWS_COUNT, 1);
            //一个ip时隔3小时访问才再次增加访问量
            redisService.set(uuid, ipAddress, 60 * 60 * 3);
        }
    }
}
