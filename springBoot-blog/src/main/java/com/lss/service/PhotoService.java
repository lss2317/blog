package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Photo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 照片服务类
 *
 * @author lss
 * @create 2022年03月12日 21:24
 */
public interface PhotoService extends IService<Photo> {

    /**
     * 添加照片进相册
     *
     * @param albumId   相册id
     * @param photoList 照片url集合
     * @param request   请求，用来获取token
     */
    Result<?> savePhotos(@Param("albumId") Integer albumId, List<String> photoList, HttpServletRequest request);

    /**
     * 获取相册的照片列表
     *
     * @param currentPage 页码
     * @param pageSize    每页展示个数
     * @param albumId     相册id
     * @param isDelete    是否删除 1、是 0、否
     */
    Result<JSONObject> listPhotos(Integer currentPage, Integer pageSize, Integer albumId, Integer isDelete);

    /**
     * 批量删除照片
     *
     * @param deletePhotoIdList 要删除的照片id集合
     * @param request           请求，用来获取token
     */
    Result<?> deletePhotos(List<Integer> deletePhotoIdList, HttpServletRequest request);

    /**
     * 移动照片到其他相册
     *
     * @param albumId           转移的相册id
     * @param selectPhotoIdList 选择的照片id集合
     * @param request           请求，用来获取token
     */
    Result<?> updatePhotos(@Param("albumId") Integer albumId, List<Integer> selectPhotoIdList, HttpServletRequest request);

    /**
     * 修改照片信息
     *
     * @param photo   照片接收
     * @param request 请求，获取token
     */
    Result<?> updatePhoto(Photo photo, HttpServletRequest request);

    /**
     * 前台获取相册照片
     *
     * @param current 页码
     * @param size    每次获取照片数
     * @param albumId 相册id
     */
    Result<JSONObject> blogListPhotos(Integer current, Integer size, Integer albumId);
}
