package com.lss.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 日志实体类
 *
 * @author lss
 * @create 2022年03月18日 22:40
 */
@Data
@TableName("blog_operation_log")
public class OperationLog {

    /**
     * 日志id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作模块
     */
    private String optModule;

    /**
     * 操作路径
     */
    private String optUrl;

    /**
     * 操作类型
     */
    private String optType;

    /**
     * 操作方法
     */
    private String optMethod;

    /**
     * 操作描述
     */
    private String optDesc;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 返回数据
     */
    private String responseData;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

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
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
}
