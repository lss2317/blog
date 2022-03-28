package com.lss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 分类
 *
 * @author lss
 * @create 2022年02月15日 14:43
 */
@Data
@TableName("blog_class")
public class Classification {

    /**
     * 分类id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 分类名称
     */
    private String classificationName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    /**
     * 文章量
     */
    @TableField(exist = false)
    private Integer articleTotal;
}
