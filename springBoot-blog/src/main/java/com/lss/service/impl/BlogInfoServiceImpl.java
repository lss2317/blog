package com.lss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.*;
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
import java.util.*;

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
    @Resource
    MessageService messageService;
    @Resource
    UniqueViewService uniqueViewService;


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
            //保存唯一标识
            redisService.sAdd(RedisPrefixConst.UNIQUE_VISITOR, UUID.randomUUID().toString());
        }
    }

    @Override
    public JSONObject getBlogBackInfo() {
        JSONObject json = new JSONObject();
        // 查询访问量
        Object count = redisService.get(RedisPrefixConst.BLOG_VIEWS_COUNT);
        Integer viewsCount = Integer.parseInt(Optional.ofNullable(count).orElse(0).toString());
        // 查询留言量
        Long messageCount = messageService.count();
        // 查询用户量
        Long userCount = userService.count();
        // 查询文章量
        Long articleCount = articleService.count(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDelete, 0));
        json.put("viewsCount", viewsCount);
        json.put("messageCount", messageCount);
        json.put("userCount", userCount);
        json.put("articleCount", articleCount);
        List<UniqueView> uniqueViews = uniqueViewService.listUniqueViews();
        json.put("uniqueViewList", uniqueViews);
        Result<JSONObject> classifications = classificationService.listClassifications();
        json.put("classList", classifications.getData().get("data"));
        // 查询redis访问量前五的文章
        Map<Object, Double> articleMap = redisService.zReverseRangeWithScore(RedisPrefixConst.ARTICLE_VIEWS_COUNT, 0, 4);
        if (CollectionUtils.isNotEmpty(articleMap)) {
            // 查询文章排行
            List<Article> articleRankList = listArticleRank(articleMap);
            json.put("articleRankList", articleRankList);
        }
        return json;
    }

    @Override
    public JSONObject getLikes() {
        JSONObject json = new JSONObject();
        try {
            String token = request.getHeader("token");
            Claims claims = JWTUtils.parseToken(token);
            Object id = claims.get("id");
            //获取点赞说说id的set集合
            Set<Object> setTalkLikes = redisService.sMembers(RedisPrefixConst.TALK_USER_LIKE + id);
            json.put("talkLikeSet", setTalkLikes);
            //获取点赞评论id的set集合
            Set<Object> setCommentLikes = redisService.sMembers(RedisPrefixConst.COMMENT_USER_LIKE + id);
            json.put("commentLikeSet", setCommentLikes);
            //获取点赞文章id的set集合
            Set<Object> articleLikeSet = redisService.sMembers(RedisPrefixConst.ARTICLE_USER_LIKE + id);
            json.put("articleLikeSet", articleLikeSet);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return json;
    }

    /**
     * 查询文章排行
     *
     * @param articleMap 文章信息
     * @return {@link List<Article>} 文章排行
     */
    private List<Article> listArticleRank(Map<Object, Double> articleMap) {
        List<Article> list = new ArrayList<>();
        // 提取文章id
        List<Integer> articleIdList = new ArrayList<>();
        articleMap.forEach((key, value) -> articleIdList.add((Integer) key));
        // 查询文章信息
        articleIdList.forEach(item -> {
            Article article = articleService.getOne(new LambdaQueryWrapper<Article>()
                    .eq(Article::getId, item)
                    .select(Article::getId, Article::getArticleTitle)
            );
            article.setViewsCount(articleMap.get(article.getId()).intValue());
            list.add(article);
        });
        //lambda表达式进行浏览量从大到小排序
        list.sort(new Comparator<Article>() {
            @Override
            public int compare(Article o1, Article o2) {
                return o2.getViewsCount() - o1.getViewsCount();
            }
        });
        return list;
    }
}
