package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.Photo;
import com.lss.entity.PhotoAlbum;
import com.lss.entity.User;
import com.lss.enums.AlbumEnum;
import com.lss.mapper.PhotoAlbumMapper;
import com.lss.service.PhotoService;
import com.lss.service.UserService;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lss
 * @create 2022年03月12日 21:22
 */
@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbum> implements com.lss.service.PhotoAlbumService {

    @Resource
    UserService userService;
    @Resource
    PhotoAlbumMapper photoAlbumMapper;
    @Resource
    PhotoService photoService;

    @Override
    public Result<?> saveOrUpdateAlbum(PhotoAlbum photoAlbum, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getAlbumResult(null, AlbumEnum.NOT_ABILITY_UPDATE);
        }
        photoAlbum.setCreateTime(new Date());
        if (photoAlbum.getId() != null) {
            UpdateWrapper<PhotoAlbum> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", photoAlbum.getId());
            updateWrapper.set("album_name", photoAlbum.getAlbumName());
            updateWrapper.set("album_desc", photoAlbum.getAlbumDesc());
            updateWrapper.set("album_cover", photoAlbum.getAlbumCover());
            updateWrapper.set("status", photoAlbum.getStatus());
            updateWrapper.set("update_time", new Date());
            boolean update = this.update(updateWrapper);
            if (!update) {
                return Result.getAlbumResult(null, AlbumEnum.UPDATE_ALBUM_ERROR);
            }
            return Result.getAlbumResult(null, AlbumEnum.UPDATE_ALBUM_SUCCESS);
        }
        boolean save = this.save(photoAlbum);
        if (!save) {
            return Result.getAlbumResult(null, AlbumEnum.SAVE_ALBUM_ERROR);
        }
        return Result.getAlbumResult(null, AlbumEnum.SAVE_ALBUM_SUCCESS);
    }

    @Override
    public Result<JSONObject> listAlbum(Integer pageSize, Integer currentPage, String keywords) {
        currentPage = (currentPage - 1) * pageSize;
        JSONObject jsonResult = new JSONObject();
        List<PhotoAlbum> albumList = photoAlbumMapper.listAlbum(pageSize, currentPage, keywords.trim());
        jsonResult.put("data", albumList);
        QueryWrapper<PhotoAlbum> queryWrapper = new QueryWrapper<>();
        if (!keywords.trim().equals("")) {
            queryWrapper.like("album_name", keywords.trim());
        }
        long count = this.count(queryWrapper);
        jsonResult.put("count", count);
        return Result.getAlbumResult(jsonResult, AlbumEnum.LIST_ALBUM_SUCCESS);
    }

    @Override
    public Result<?> deleteAlbum(Integer id, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getAlbumResult(null, AlbumEnum.NOT_ABILITY_UPDATE);
        }
        //删除相册里面的图片,进入回收站
        UpdateWrapper<Photo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("album_id", id);
        updateWrapper.set("is_delete", 1);
        photoService.update(updateWrapper);
        //删除相册
        boolean remove = this.removeById(id);
        if (!remove) {
            Result.getAlbumResult(null, AlbumEnum.DELETE_ALBUM_ERROR);
        }
        return Result.getAlbumResult(null, AlbumEnum.DELETE_ALBUM_SUCCESS);
    }
}
