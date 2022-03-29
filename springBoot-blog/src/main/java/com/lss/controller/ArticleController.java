package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Article;
import com.lss.entity.TagAndArticle;
import com.lss.enums.ArticleEnum;
import com.lss.service.ArticleService;
import com.lss.service.TagAndArticleService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章控制器
 *
 * @author lss
 * @create 2022年02月23日 14:35
 */
@RestController
@RequestMapping("article")
public class ArticleController {

    @Resource
    ArticleService articleService;
    @Resource
    TagAndArticleService tagAndArticleService;
    @Resource
    HttpServletRequest request;

    /**
     * 新增或者修改文章
     */
    @Transactional
    @PostMapping("saveArticle")
    @OptLog(ApiModule = "文章模块", ApiDesc = "添加或修改文章", optType = OptTypeConst.SAVE_OR_UPDATE)
    public Result<?> saveArticle(@RequestBody Article article) {
        return articleService.saveOrUpdateArticle(article, request);
    }

    /**
     * 文章列表
     *
     * @param currentPage        页码
     * @param pageSize           每页显示条数
     * @param articleType        文章类型
     * @param classificationName 文章分类名称
     * @param tagName            标签名称
     * @param keywords           文章标题关键词
     * @param deleteStatus       是否被删除了
     * @param status             公布状态，公开还是私有
     */
    @GetMapping("listArticle")
    public Result<JSONObject> listArticle(@RequestParam("currentPage") Integer currentPage,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestParam(defaultValue = "4") Integer articleType,
                                          @RequestParam(defaultValue = "") String classificationName,
                                          @RequestParam(defaultValue = "") String tagName,
                                          @RequestParam(defaultValue = "") String keywords,
                                          @RequestParam Integer deleteStatus,
                                          @RequestParam Integer status) {
        JSONObject json = new JSONObject();
        long count = articleService.getCount(articleType, classificationName, tagName, keywords.trim(), deleteStatus, status);
        json.put("count", count);
        currentPage = (currentPage - 1) * pageSize;
        List<Article> list = articleService.listArticle(currentPage, pageSize, articleType, classificationName, tagName, keywords.trim(), deleteStatus, status);
        json.put("data", list);
        return Result.getArticleResult(json, ArticleEnum.SEARCH_ARTICLE_SUCCESS);
    }

    /**
     * 删除文章
     */
    @GetMapping("deleteArticle")
    @OptLog(ApiModule = "文章模块", ApiDesc = "删除文章", optType = OptTypeConst.REMOVE)
    public Result<?> deleteArticle(@RequestParam("id") Integer id, @RequestParam("deleteStatus") Integer deleteStatus) {
        return articleService.deleteArticle(id, deleteStatus, request);
    }

    /**
     * 恢复文章
     */
    @GetMapping("restoreArticle")
    @OptLog(ApiModule = "文章模块", ApiDesc = "恢复文章", optType = OptTypeConst.RESTORE)
    public Result<?> restoreArticle(@RequestParam("id") Integer id, HttpServletRequest request) {
        return articleService.restoreArticle(id, request);
    }

    /**
     * 批量删除文章
     */
    @Transactional
    @DeleteMapping("deleteArticles")
    @OptLog(ApiModule = "文章模块", ApiDesc = "批量删除文章", optType = OptTypeConst.REMOVE)
    public Result<?> deleteArticles(@RequestBody List<Integer> articleIdList) {
        return articleService.deleteArticles(articleIdList, request);
    }


    /**
     * 文章置顶
     */
    @GetMapping("changeTop")
    @OptLog(ApiModule = "文章模块", ApiDesc = "文章置顶", optType = OptTypeConst.UPDATE)
    public Result<?> changeTop(@RequestParam Integer id, @RequestParam Integer isTop) {
        return articleService.changeTop(id, isTop, request);
    }

    /**
     * 后台获取单个文章
     */
    @GetMapping("/admin/{articleId}")
    public Result<?> selectArticle(@PathVariable Integer articleId) {
        Article article = articleService.getById(articleId);
        QueryWrapper<TagAndArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        List<TagAndArticle> list = tagAndArticleService.list(queryWrapper);
        List<String> tagNames = new ArrayList<>();
        for (TagAndArticle tagAndArticle : list) {
            tagNames.add(tagAndArticle.getTagName());
        }
        article.setTagNames(tagNames);
        return Result.getArticleResult(article, ArticleEnum.SEARCH_ARTICLE_SUCCESS);
    }

    /**
     * 查看首页文章
     */
    @GetMapping("articles")
    public Result<?> articles() {
        return articleService.articles();
    }

    /**
     * 获取前台归档信息
     *
     * @return 归档集合数据json字符串
     */
    @GetMapping("archives")
    public Result<JSONObject> archives(@RequestParam Integer current, @RequestParam Integer pageSize) {
        return articleService.archives(current, pageSize);
    }

    /**
     * 根据条件获取文章列表
     *
     * @param classificationId 分类id
     * @param tagId            标签id
     */
    @GetMapping("condition")
    public Result<JSONObject> listByCondition(Integer classificationId, Integer tagId) {
        return articleService.listArticleByCondition(classificationId, tagId);
    }

    /**
     * 根据id查看文章
     *
     * @param articleId 文章id
     * @return {@link Result<Article>} 文章信息
     */
    @GetMapping("/{articleId}")
    public Result<Article> getArticleById(@PathVariable("articleId") Integer articleId) {
        Article article = articleService.getArticleById(articleId);
        return Result.getArticleResult(article, ArticleEnum.SEARCH_ARTICLE_SUCCESS);
    }
}
