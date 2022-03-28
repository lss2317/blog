package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.PhotoAlbum;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;

/**
 * 相册服务类
 *
 * @author lss
 * @create 2022年03月12日 21:22
 */
public interface PhotoAlbumService extends IService<PhotoAlbum> {

    /**
     * 添加或修改相册
     *
     * @param photoAlbum 相册实体类
     * @param request    请求，用来获取token
     */
    Result<?> saveOrUpdateAlbum(PhotoAlbum photoAlbum, HttpServletRequest request);

    /**
     * 分页查询相册列表
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     * @param keywords    相册名关键词
     */
    Result<JSONObject> listAlbum(Integer pageSize, Integer currentPage, String keywords);

    /**
     * 删除相册
     *
     * @param id      相册id
     * @param request 请求，获取token
     */
    Result<?> deleteAlbum(@Param("id") Integer id, HttpServletRequest request);
}
