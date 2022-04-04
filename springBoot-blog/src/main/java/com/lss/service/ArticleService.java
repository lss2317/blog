package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文章服务类
 *
 * @author lss
 * @create 2022年02月20日 20:30
 */
public interface ArticleService extends IService<Article> {

    /**
     * 查询文章列表
     *
     * @param currentPage        页码
     * @param pageSize           每页显示条数
     * @param articleType        文章类型
     * @param classificationName 文章分类名称
     * @param tagName            标签名称
     * @param keywords           文章标题关键词
     * @param deleteStatus       是否删除 0否 1是
     * @param status             状态值 1公开 2私密 3评论可见
     */
    List<Article> listArticle(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, Integer articleType, String classificationName, String tagName, String keywords, Integer deleteStatus, Integer status);


    /**
     * 获取分类文章总数
     *
     * @param articleType        文章类型
     * @param classificationName 分类名称
     * @param tagName            标签名称
     * @param keywords           文章标题关键词
     * @param deleteStatus       是否删除 0否 1是
     * @param status             状态值 1公开 2私密 3评论可见
     */
    Integer getCount(Integer articleType, String classificationName, String tagName, String keywords, Integer deleteStatus, Integer status);

    /**
     * 新增或者修改文章
     *
     * @param article 文章实体类
     * @param request 请求，获取token
     */
    Result<?> saveOrUpdateArticle(@RequestBody Article article, HttpServletRequest request);

    /**
     * 删除文章
     *
     * @param id           文章id
     * @param deleteStatus 文章状态
     * @param request      请求，用来获取token
     */
    Result<?> deleteArticle(@Param("id") Integer id, @Param("deleteStatus") Integer deleteStatus, HttpServletRequest request);

    /**
     * 恢复文章
     *
     * @param id      文章id
     * @param request 请求，用来获取token
     */
    Result<?> restoreArticle(@Param("id") Integer id, HttpServletRequest request);

    /**
     * 批量删除文章
     *
     * @param articleIdList 要删除的文章id集合
     * @param request       请求，用来获取token
     */
    Result<?> deleteArticles(List<Integer> articleIdList, HttpServletRequest request);

    /**
     * 文章置顶
     *
     * @param id      文章id
     * @param isTop   置顶状态
     * @param request 请求，获取token
     */
    Result<?> changeTop(@Param("id") Integer id, @Param("isTop") Integer isTop, HttpServletRequest request);

    /**
     * 前台页面获取文章列表
     *
     * @return json数据
     */
    Result<?> articles();

    /**
     * 前台获取归档信息
     *
     * @param current  页码
     * @param pageSize 每页展示总数
     * @return 文章归档集合json数据
     */
    Result<JSONObject> archives(Integer current, Integer pageSize);


    /**
     * 按照条件获取文章列表
     *
     * @param classificationId 分类id
     * @param tagId            标签id
     * @return json格式数据
     */
    Result<JSONObject> listArticleByCondition(Integer classificationId, Integer tagId);

    /**
     * 根据id查看文章
     *
     * @param articleId 文章id
     * @return {@link Article} 文章信息
     */
    Article getArticleById(Integer articleId);

    /**
     * 点赞文章
     *
     * @param articleId 文章id
     */
    void saveArticleLike(Integer articleId);

    /**
     * 搜索文章
     *
     * @param keywords 条件
     * @return 文章列表
     */
    Result<List<Article>> listArticlesBySearch(String keywords);
}
