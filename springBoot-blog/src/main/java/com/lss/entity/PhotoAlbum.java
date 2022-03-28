package com.lss.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 相册
 *
 * @author lss
 * @create 2022年03月12日 20:51
 */
@Data
@TableName("blog_album")
public class PhotoAlbum {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 相册名
     */
    private String albumName;

    /**
     * 相册描述
     */
    private String albumDesc;

    /**
     * 相册封面
     */
    private String albumCover;

    /**
     * 状态值 1公开 2私密
     */
    private Integer status;

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

}
