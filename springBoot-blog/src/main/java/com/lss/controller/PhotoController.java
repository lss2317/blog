package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Photo;
import com.lss.service.PhotoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 照片控制层
 *
 * @author lss
 * @create 2022年03月13日 22:52
 */
@RestController
@RequestMapping("photo")
public class PhotoController {

    @Resource
    PhotoService photoService;
    @Resource
    HttpServletRequest request;

    /**
     * 批量添加照片进相册
     *
     * @param photo 接收参数
     */
    @Transactional
    @PostMapping("savePhotos")
    @OptLog(ApiModule = "照片模块", ApiDesc = "批量添加照片", optType = OptTypeConst.SAVE)
    public Result<?> savePhotos(@RequestBody Photo photo) {
        return photoService.savePhotos(photo.getAlbumId(), photo.getPhotoList(), request);
    }

    /**
     * 获取相册的照片列表
     *
     * @param currentPage 页码
     * @param pageSize    每页展示个数
     * @param albumId     相册id
     * @param isDelete    是否删除 1、是 0、否
     */
    @GetMapping("listPhotos")
    public Result<JSONObject> listPhotos(@RequestParam("currentPage") Integer currentPage,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam(required = false) Integer albumId,
                                         @RequestParam("isDelete") Integer isDelete) {
        return photoService.listPhotos(currentPage, pageSize, albumId, isDelete);
    }

    /**
     * 批量删除照片
     */
    @DeleteMapping("deletePhotos")
    @OptLog(ApiModule = "照片模块", ApiDesc = "批量删除照片", optType = OptTypeConst.REMOVE)
    public Result<?> deletePhotos(@RequestBody List<Integer> selectPhotoIdList) {
        return photoService.deletePhotos(selectPhotoIdList, request);
    }

    /**
     * 移动照片到其他相册
     *
     * @param photo 接收实体类
     */
    @PutMapping("updatePhotos")
    @OptLog(ApiModule = "照片模块", ApiDesc = "移动照片", optType = OptTypeConst.SAVE)
    public Result<?> updatePhotos(@RequestBody Photo photo) {
        return photoService.updatePhotos(photo.getAlbumId(), photo.getSelectPhotoIdList(), request);
    }

    /**
     * 修改照片信息
     *
     * @param photo 接收照片信息实体类
     */
    @PutMapping("updatePhoto")
    @OptLog(ApiModule = "照片模块", ApiDesc = "修改照片信息", optType = OptTypeConst.UPDATE)
    public Result<?> updatePhoto(@RequestBody Photo photo) {
        return photoService.updatePhoto(photo, request);
    }

    /**
     * 前台获取相册照片
     */
    @GetMapping("blogListPhotos")
    public Result<?> blogListPhotos(Integer current, Integer size, Integer albumId) {
        return photoService.blogListPhotos(current, size, albumId);
    }
}
