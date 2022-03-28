package com.lss.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 页面管理
 *
 * @author lss
 * @create 2022年03月17日 10:48
 */
@Data
@TableName("blog_page")
public class Page {

    /**
     * 页面id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 页面名
     */
    private String pageName;

    /**
     * 页面标签
     */
    private String pageLabel;

    /**
     * 页面封面
     */
    private String pageCover;

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
