package com.lss.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 网站配置信息
 *
 * @author lss
 * @create 2022年03月18日 17:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("blog_website_config")
public class WebsiteConfig {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置信息
     */
    private String config;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 网站头像
     */
    @TableField(exist = false)
    private String websiteAvatar;

    /**
     * 网站名称
     */
    @TableField(exist = false)
    private String websiteName;

    /**
     * 网站作者
     */
    @TableField(exist = false)
    private String websiteAuthor;

    /**
     * 网站介绍
     */
    @TableField(exist = false)
    private String websiteIntro;

    /**
     * 网站公告
     */
    @TableField(exist = false)
    private String websiteNotice;

    /**
     * 网站创建时间
     */
    @TableField(exist = false)
    private String websiteCreateTime;

    /**
     * 网站备案号
     */
    @TableField(exist = false)
    private String websiteRecordNo;

    /**
     * 社交登录列表
     */
    @TableField(exist = false)
    private List<String> socialLoginList;

    /**
     * 社交url列表
     */
    @TableField(exist = false)
    private List<String> socialUrlList;

    /**
     * qq
     */
    @TableField(exist = false)
    private String qq;

    /**
     * github
     */
    @TableField(exist = false)
    private String github;

    /**
     * gitee
     */
    @TableField(exist = false)
    private String gitee;

    /**
     * 游客头像
     */
    @TableField(exist = false)
    private String touristAvatar;

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String userAvatar;

    /**
     * 是否评论审核
     */
    @TableField(exist = false)
    private Integer isCommentReview;

    /**
     * 是否留言审核
     */
    @TableField(exist = false)
    private Integer isMessageReview;

    /**
     * 是否邮箱通知
     */
    @TableField(exist = false)
    private Integer isEmailNotice;

    /**
     * 是否打赏
     */
    @TableField(exist = false)
    private Integer isReward;

    /**
     * 微信二维码
     */
    @TableField(exist = false)
    private String weiXinQRCode;

    /**
     * 支付宝二维码
     */
    @TableField(exist = false)
    private String alipayQRCode;

    /**
     * 是否开启聊天室
     */
    @TableField(exist = false)
    private Integer isChatRoom;

    /**
     * websocket地址
     */
    @TableField(exist = false)
    private String websocketUrl;

    /**
     * 是否开启音乐
     */
    @TableField(exist = false)
    private Integer isMusicPlayer;
}
