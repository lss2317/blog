package com.lss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 标签
 *
 * @author lss
 * @create 2022年02月11日 15:56
 */
@Data
@TableName("blog_tag")
public class Tag {
    /**
     * 标签id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    /**
     * 文章量
     */
    @TableField(exist = false)
    private Integer articleTotal;

}