package com.lss.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 用户
 *
 * @author lss
 * @create 2022年02月27日 19:45
 */
@Data
@TableName("blog_user")
public class User {

    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * QQ登录openId 唯一
     */
    private String qqOpenId;

    /**
     * 微博登录用户id
     */
    private String weiboId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户角色  0普通用户 1测试账号 2管理员
     */
    private Integer role;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 个人简介
     */
    private String intro;

    /**
     * 用户登录ip
     */
    private String ipAddress;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 登录类型 1 邮箱，2 QQ， 3 微博
     */
    private Integer loginType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新个人信息时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /**
     * 上次登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    /**
     * 是否禁用 0否 1禁
     */
    private Integer isDisable;

    /**
     * 点赞文章集合
     */
    @TableField(exist = false)
    private Set<Object> articleLikeSet;

    /**
     * 点赞评论集合
     */
    @TableField(exist = false)
    private Set<Object> commentLikeSet;

    /**
     * 点赞说说集合
     */
    @TableField(exist = false)
    private Set<Object> talkLikeSet;

}
