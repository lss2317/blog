package com.lss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author lss
 * @create 2022年02月23日 15:54
 */
@Data
@TableName("blog_tag_article")
public class TagAndArticle {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 文章id
     */
    private Integer articleId;
    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 标签id
     */
    @TableField(exist = false)
    private Integer tagId;
}
