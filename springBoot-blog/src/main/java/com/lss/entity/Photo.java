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
 * 照片
 *
 * @author lss
 * @create 2022年03月12日 20:53
 */
@Data
@TableName("blog_photo")
public class Photo {
    /**
     * 照片id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 相册id
     */
    private Integer albumId;

    /**
     * 照片名
     */
    private String photoName;

    /**
     * 照片描述
     */
    private String photoDesc;

    /**
     * 照片地址
     */
    private String photoSrc;

    /**
     * 是否删除 1、是 0、否
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 临时获取添加照片url集合
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * 临时获取移动照片id集合
     */
    @TableField(exist = false)
    private List<Integer> selectPhotoIdList;
}
