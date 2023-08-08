package com.lss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文章
 *
 * @author lss
 * @create 2022年02月11日 15:03
 */
@Data
@TableName("blog_article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文章分类
     */
    private String classificationName;

    /**
     * 文章作者
     */
    private Integer userId;

    /**
     * 文章图片
     */
    private String articleCover;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 文章类型 1原创 2转载 3翻译
     */
    private Integer type;

    /**
     * 原文链接
     */
    private String originalUrl;

    /**
     * 是否置顶 0否 1是
     */
    private Integer isTop;

    /**
     * 是否删除 0否 1是
     */
    private Integer isDelete;

    /**
     * 状态值 1公开 2私密 3评论可见
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;
    /**
     * 文章标签
     */
    @TableField(exist = false)
    private List<String> tagNames;
    /**
     * 文章标签名称集合
     */
    @TableField(exist = false)
    private List<TagAndArticle> tagNameList;

    /**
     * 文章分类id
     */
    @TableField(exist = false)
    private Integer classId;

    /**
     * 点赞量
     */
    @TableField(exist = false)
    private Integer likeCount;
    /**
     * 浏览量
     */
    @TableField(exist = false)
    private Integer viewsCount;

    /**
     * 文章标签
     */
    @TableField(exist = false)
    private List<Tag> tagDTOList;

    /**
     * 上一篇文章
     */
    @TableField(exist = false)
    private Article lastArticle;

    /**
     * 下一篇文章
     */
    @TableField(exist = false)
    private Article nextArticle;

    /**
     * 推荐文章列表
     */
    @TableField(exist = false)
    private List<Article> recommendArticleList;

    /**
     * 最新文章列表
     */
    @TableField(exist = false)
    private List<Article> newestArticleList;
}
