package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.*;
import com.lss.enums.ArticleEnum;
import com.lss.mapper.ArticleMapper;
import com.lss.service.*;
import com.lss.utils.BeanCopyUtils;
import com.lss.utils.IpUtils;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author lss
 * @create 2022年02月20日 20:30
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    ArticleMapper articleMapper;
    @Resource
    UserService userService;
    @Resource
    TagAndArticleService tagAndArticleService;
    @Resource
    ClassificationService classificationService;
    @Resource
    TagService tagService;
    @Resource
    RedisService redisService;
    @Resource
    HttpServletRequest request;

    @Override
    public List<Article> listArticle(Integer currentPage, Integer pageSize, Integer articleType,
                                     String classificationName, String tagName, String keywords, Integer deleteStatus, Integer status) {
        List<Article> articleList = articleMapper.listArticle(currentPage, pageSize, articleType, classificationName,
                tagName, keywords, deleteStatus, status);
        // 查询文章点赞量和浏览量
        Map<Object, Double> viewsCountMap = redisService.zAllScore(RedisPrefixConst.ARTICLE_VIEWS_COUNT);
        Map<String, Object> likeCountMap = redisService.hGetAll(RedisPrefixConst.ARTICLE_LIKE_COUNT);
        // 封装点赞量和浏览量
        articleList.forEach(item -> {
            Double viewsCount = viewsCountMap.get(item.getId());
            if (Objects.nonNull(viewsCount)) {
                item.setViewsCount(viewsCount.intValue());
            }
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
        });
        return articleList;
    }

    @Override
    public Integer getCount(Integer articleType, String classificationName, String tagName, String keywords, Integer deleteStatus, Integer status) {
        return articleMapper.getCount(articleType, classificationName, tagName, keywords, deleteStatus, status);
    }

    @Override
    public Result<?> saveOrUpdateArticle(Article article, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object id = claims.get("id");
        User user = userService.getById((Serializable) id);
        if (user.getRole() != 2) {
            return Result.getArticleResult(null, ArticleEnum.NOT_ABILITY_UPDATE);
        }
        //修改文章
        if (article.getId() != null) {
            UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", article.getId());
            updateWrapper.set("classification_name", article.getClassificationName());
            updateWrapper.set("article_cover", article.getArticleCover());
            updateWrapper.set("article_title", article.getArticleTitle());
            updateWrapper.set("article_content", article.getArticleContent());
            updateWrapper.set("type", article.getType());
            updateWrapper.set("original_url", article.getOriginalUrl());
            updateWrapper.set("is_top", article.getIsTop());
            updateWrapper.set("status", article.getStatus());
            updateWrapper.set("update_time", new Date());
            boolean update = this.update(updateWrapper);
            //修改标签
            List<String> tagNames = article.getTagNames();
            List<TagAndArticle> list = tagAndArticleService.list(new QueryWrapper<TagAndArticle>().eq("article_id", article.getId()));
            for (int i = 0; i < list.size(); i++) {
                UpdateWrapper<TagAndArticle> wrapper = new UpdateWrapper<>();
                wrapper.eq("id", list.get(i).getId());
                wrapper.set("tag_name", tagNames.get(i));
                tagAndArticleService.update(wrapper);
            }
            if (!update) {
                return Result.getArticleResult(null, ArticleEnum.MODIFY_ARTICLE_ERROR);
            }
            return Result.getArticleResult(null, ArticleEnum.MODIFY_ARTICLE_SUCCESS);
        }
        //添加文章
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        article.setUserId(user.getId());
        boolean save = this.save(article);
        if (!save) {
            return Result.getArticleResult(null, ArticleEnum.SAVE_ARTICLE_ERROR);
        }
        //查询文章id
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_title", article.getArticleTitle());
        queryWrapper.eq("article_content", article.getArticleContent());
        queryWrapper.eq("article_cover", article.getArticleCover());
        Article one = this.getOne(queryWrapper);
        //新文章id
        Integer saveId = one.getId();
        //保存标签与文章的联系
        for (String tagName : article.getTagNames()) {
            TagAndArticle tagAndArticle = new TagAndArticle();
            tagAndArticle.setArticleId(saveId);
            tagAndArticle.setTagName(tagName);
            tagAndArticleService.save(tagAndArticle);
        }
        return Result.getArticleResult(null, ArticleEnum.SAVE_ARTICLE_SUCCESS);
    }

    @Override
    public Result<?> deleteArticle(Integer id, Integer deleteStatus, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getArticleResult(null, ArticleEnum.NOT_ABILITY_UPDATE);
        }
        //如果删除状态为1，设置 id_delete 为1，进入回收站
        if (deleteStatus == 0) {
            UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id);
            updateWrapper.set("is_delete", 1);
            boolean update = this.update(updateWrapper);
            UpdateWrapper<TagAndArticle> wrapper = new UpdateWrapper<>();
            wrapper.set("is_delete", 1);
            wrapper.eq("article_id", id);
            tagAndArticleService.update(wrapper);
            if (!update) {
                return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_ERROR);
            }
            return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_SUCCESS);
        }
        //彻底删除
        boolean remove = this.removeById(id);
        QueryWrapper<TagAndArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);
        tagAndArticleService.remove(queryWrapper);
        if (!remove) {
            return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_ERROR);
        }
        return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_SUCCESS);
    }

    @Override
    public Result<?> restoreArticle(Integer id, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getArticleResult(null, ArticleEnum.NOT_ABILITY_UPDATE);
        }
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_delete", 0);
        boolean update = this.update(updateWrapper);
        //恢复标签
        UpdateWrapper<TagAndArticle> wrapper = new UpdateWrapper<>();
        wrapper.set("is_delete", 0);
        wrapper.eq("article_id", id);
        tagAndArticleService.update(wrapper);
        if (!update) {
            return Result.getArticleResult(null, ArticleEnum.RESTORE_ARTICLE_ERROR);
        }
        return Result.getArticleResult(null, ArticleEnum.RESTORE_ARTICLE_SUCCESS);
    }

    @Override
    public Result<?> deleteArticles(List<Integer> articleIdList, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getArticleResult(null, ArticleEnum.NOT_ABILITY_UPDATE);
        }
        if (articleIdList.isEmpty()) {
            return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_SUCCESS);
        }
        Integer first = articleIdList.get(0);
        Article firstArticle = this.getById(first);
        if (firstArticle.getIsDelete() == 0) {
            //虚假删除
            for (Integer integer : articleIdList) {
                UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("is_delete", 1);
                updateWrapper.eq("id", integer);
                this.update(updateWrapper);
                //标签删除
                UpdateWrapper<TagAndArticle> wrapper = new UpdateWrapper<>();
                wrapper.set("is_delete", 1);
                wrapper.eq("article_id", integer);
                tagAndArticleService.update(wrapper);
            }
        } else {
            //彻底删除
            boolean removes = this.removeBatchByIds(articleIdList);
            for (Integer integer : articleIdList) {
                QueryWrapper<TagAndArticle> wrapper = new QueryWrapper<>();
                wrapper.eq("article_id", integer);
                tagAndArticleService.remove(wrapper);
            }
            if (!removes) {
                return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_ERROR);
            }
        }
        return Result.getArticleResult(null, ArticleEnum.DELETE_ARTICLE_SUCCESS);
    }

    @Override
    public Result<?> changeTop(Integer id, Integer isTop, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getArticleResult(null, ArticleEnum.NOT_ABILITY_UPDATE);
        }
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_top", isTop);
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getArticleResult(null, ArticleEnum.CHANGE_TOP_ERROR);
        }
        return Result.getArticleResult(null, ArticleEnum.CHANGE_TOP_SUCCESS);
    }

    @Override
    public Result<?> articles() {
        List<Article> articles = articleMapper.articles();
        List<Article> articleList = new ArrayList<>();
        for (Article article : articles) {
            if (article.getIsTop() == 1) {
                articleList.add(article);
            }
        }
        for (Article article : articles) {
            if (article.getIsTop() == 0) {
                articleList.add(article);
            }
        }
        return Result.getArticleResult(articleList, ArticleEnum.SEARCH_ARTICLE_SUCCESS);
    }

    @Override
    public Result<JSONObject> archives(Integer current, Integer pageSize) {
        current = (current - 1) * pageSize;
        List<Article> archives = articleMapper.archives(current, pageSize);
        JSONObject json = new JSONObject();
        json.put("data", archives);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.eq("status", 1);
        long count = this.count(queryWrapper);
        json.put("count", count);
        return Result.getArticleResult(json, ArticleEnum.SEARCH_ARTICLE_SUCCESS);
    }

    @Override
    public Result<JSONObject> listArticleByCondition(Integer classificationId, Integer tagId) {
        JSONObject json = new JSONObject();
        if (classificationId != null) {
            Classification classification = classificationService.getById(classificationId);
            List<Article> articleList = articleMapper.listByCondition(classification.getClassificationName(), "");
            json.put("data", articleList);
            json.put("name", classification.getClassificationName());
        } else {
            Tag tag = tagService.getById(tagId);
            List<Article> articleList = articleMapper.listByCondition("", tag.getTagName());
            json.put("data", articleList);
            json.put("name", tag.getTagName());
        }
        return Result.getArticleResult(json, ArticleEnum.SEARCH_ARTICLE_SUCCESS);
    }

    @Override
    public Article getArticleById(Integer articleId) {
        // 查询推荐文章
        CompletableFuture<List<Article>> recommendArticleList = CompletableFuture
                .supplyAsync(() -> articleMapper.listRecommendArticles(articleId));
        // 查询最新文章
        CompletableFuture<List<Article>> newestArticleList = CompletableFuture
                .supplyAsync(() ->
                        articleMapper.selectList(new LambdaQueryWrapper<Article>()
                                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover, Article::getCreateTime)
                                .eq(Article::getIsDelete, 0)
                                .eq(Article::getStatus, 1)
                                .ne(Article::getId, articleId)
                                .orderByDesc(Article::getId)
                                .last("limit 5")));
        // 查询id对应文章
        Article article = articleMapper.getArticleById(articleId);
        if (Objects.isNull(article)) {
            throw new NullPointerException("文章不存在");
        }
        // 更新文章浏览量
        updateArticleViewsCount(articleId);
        // 查询上一篇下一篇文章
        Article lastArticle = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getStatus, 1)
                .lt(Article::getId, articleId)
                .orderByDesc(Article::getId)
                .last("limit 1"));
        Article nextArticle = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getStatus, 1)
                .gt(Article::getId, articleId)
                .orderByAsc(Article::getId)
                .last("limit 1"));
        article.setLastArticle(BeanCopyUtils.copyObject(lastArticle, Article.class));
        article.setNextArticle(BeanCopyUtils.copyObject(nextArticle, Article.class));
        // 封装点赞量和浏览量
        Double score = redisService.zScore(RedisPrefixConst.ARTICLE_VIEWS_COUNT, articleId);
        if (Objects.nonNull(score)) {
            article.setViewsCount(score.intValue());
        }
        article.setLikeCount((Integer) redisService.hGet(RedisPrefixConst.ARTICLE_LIKE_COUNT, articleId.toString()));
        // 封装文章信息
        try {
            article.setRecommendArticleList(recommendArticleList.get());
            article.setNewestArticleList(newestArticleList.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    /**
     * 更新文章浏览量
     *
     * @param articleId 文章id
     */
    @Async
    public void updateArticleViewsCount(Integer articleId) {
        // 获取ip
        String ipAddress = IpUtils.getIpAddress(request);
        // 访客唯一标识 ，前缀 + ip地址
        String uuid = RedisPrefixConst.ARTICLE_VISITOR + "_" + ipAddress;
        Object viewUser = redisService.get(uuid);
        if (viewUser == null) {
            // 浏览量+1
            redisService.zIncr(RedisPrefixConst.ARTICLE_VIEWS_COUNT, articleId, 1D);
            //一个ip时隔3小时访问才再次增加访问量
            redisService.set(uuid, ipAddress, 60 * 60 * 3);
        }
    }
}
