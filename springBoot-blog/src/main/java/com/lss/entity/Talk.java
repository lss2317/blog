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
 * 说说
 *
 * @author lss
 * @create 2022年03月10日 20:53
 */
@Data
@TableName("blog_talk")
public class Talk {

    /**
     * 说说id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

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
     * 说说内容
     */
    private String content;

    /**
     * 图片
     */
    private String images;

    /**
     * 图片列表
     */
    @TableField(exist = false)
    private List<String> imgList;

    /**
     * 是否置顶 1.是 0.否
     */
    private Integer isTop;

    /**
     * 状态 1.公开 2.私密
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 点赞量
     */
    @TableField(exist = false)
    private Integer likeCount;

    /**
     * 评论量
     */
    @TableField(exist = false)
    private Integer commentCount;
}
