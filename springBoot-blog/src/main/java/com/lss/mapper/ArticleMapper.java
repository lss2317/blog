package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.Article;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章交互层
 *
 * @author lss
 * @create 2022年02月20日 20:17
 */
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询文章列表
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
    List<Article> listArticle(@Param("currentPage") Integer currentPage,
                              @Param("pageSize") Integer pageSize,
                              Integer articleType,
                              String classificationName,
                              String tagName, String keywords, Integer deleteStatus, Integer status);

    /**
     * 获取分类文章总数
     *
     * @param articleType        文章类型
     * @param classificationName 文章分类名称
     * @param tagName            标签名称
     * @param keywords           文章标题关键词
     * @param deleteStatus       是否被删除了
     * @param status             公布状态，公开还是私有
     */
    Integer getCount(Integer articleType, String classificationName, String tagName, String keywords, Integer deleteStatus, Integer status);

    /**
     * 前台获取文章列表
     *
     * @return 总列表数据
     */
    List<Article> articles();

    /**
     * 获取文章部分信息
     *
     * @return 数据List集合
     */
    List<Article> archives(Integer current, Integer pageSize);

    /**
     * 根据条件查询文章列表
     *
     * @param classificationName 分类名称
     * @param tagName            标签名称
     * @return 数据List集合
     */
    List<Article> listByCondition(String classificationName, String tagName);
}
