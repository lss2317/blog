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
 * 评论
 *
 * @author lss
 * @create 2022年03月26日 15:15
 */
@Data
@TableName("blog_comment")
public class Comment {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评论用户Id
     */
    private Integer userId;

    /**
     * 回复用户id
     */
    private Integer replyUserId;

    /**
     * 评论说说id
     */
    private Integer talkId;

    /**
     * 评论文章id
     */
    private Integer articleId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 父评论id
     */
    private Integer parentId;

    /**
     * 评论类型 1.文章 2.友链 3.说说
     */
    private Integer type;

    /**
     * 是否审核 0是 1否
     */
    private Integer isReview;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 昵称
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 头像
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 点赞数
     */
    @TableField(exist = false)
    private Integer likeCount;

    /**
     * 回复量
     */
    @TableField(exist = false)
    private Integer replyCount;

    /**
     * 回复列表
     */
    @TableField(exist = false)
    private List<Comment> replyDTOList;

    /**
     * 被回复用户昵称
     */
    @TableField(exist = false)
    private String replyNickname;

    /**
     * 评论文章标题
     */
    @TableField(exist = false)
    private String articleTitle;
}
