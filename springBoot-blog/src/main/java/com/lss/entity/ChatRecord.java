package com.lss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author lss
 * @create 2022年04月12日 14:53
 */
@Data
@TableName("blog_chat_record")
public class ChatRecord {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 类型
     * 1、在线人数
     * 2、历史记录
     * 3、发送消息
     * 4、撤回消息
     * 5、语音消息
     * 6、心跳消息
     */
    private Integer type;

    /**
     * 用户登录ip
     */
    private String ipAddress;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 聊天记录
     */
    @TableField(exist = false)
    private List<ChatRecord> chatRecordList;

    /**
     * 文件
     */
    @TableField(exist = false)
    private MultipartFile file;
}
