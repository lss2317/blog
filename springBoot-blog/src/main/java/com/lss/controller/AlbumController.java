package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Photo;
import com.lss.entity.PhotoAlbum;
import com.lss.enums.AlbumEnum;
import com.lss.service.PhotoAlbumService;
import com.lss.service.PhotoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 相册控制层
 *
 * @author lss
 * @create 2022年03月13日 15:26
 */
@RestController
@RequestMapping("album")
public class AlbumController {

    @Resource
    PhotoAlbumService photoAlbumService;
    @Resource
    PhotoService photoService;
    @Resource
    HttpServletRequest request;

    /**
     * 添加或修改相册
     *
     * @param photoAlbum 相册实体类
     */
    @OptLog(ApiModule = "相册模块", ApiDesc = "添加或修改相册", optType = OptTypeConst.SAVE_OR_UPDATE)
    @PostMapping("saveOrUpdate")
    public Result<?> saveOrUpdate(@RequestBody PhotoAlbum photoAlbum) {
        return photoAlbumService.saveOrUpdateAlbum(photoAlbum, request);
    }

    /**
     * 获取相册列表
     */
    @GetMapping("listAlbum")
    public Result<JSONObject> listAlbum(Integer pageSize, Integer currentPage, String keywords) {
        return photoAlbumService.listAlbum(pageSize, currentPage, keywords);
    }

    /**
     * 删除相册
     */
    @Transactional
    @GetMapping("deleteAlbum")
    @OptLog(ApiModule = "相册模块", ApiDesc = "删除相册", optType = OptTypeConst.REMOVE)
    public Result<?> deleteAlbum(@RequestParam Integer id) {
        return photoAlbumService.deleteAlbum(id, request);
    }

    /**
     * 获取单个相册信息
     */
    @GetMapping("/{albumId}")
    public Result<JSONObject> getOneAlbum(@PathVariable Integer albumId) {
        JSONObject json = new JSONObject();
        PhotoAlbum album = photoAlbumService.getById(albumId);
        QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("album_id", albumId);
        long count = photoService.count(queryWrapper);
        json.put("data", album);
        json.put("count", count);
        return Result.getAlbumResult(json, AlbumEnum.LIST_ALBUM_SUCCESS);
    }

    /**
     * 获取所有相册列表
     */
    @GetMapping("listAlbums")
    public Result<List<PhotoAlbum>> listAlbums() {
        QueryWrapper<PhotoAlbum> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        List<PhotoAlbum> list = photoAlbumService.list(queryWrapper);
        return Result.getAlbumResult(list, AlbumEnum.LIST_ALBUM_SUCCESS);
    }
}
