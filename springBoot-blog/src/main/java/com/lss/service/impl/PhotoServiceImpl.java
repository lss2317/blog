package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.Photo;
import com.lss.entity.PhotoAlbum;
import com.lss.entity.User;
import com.lss.enums.PhotoEnum;
import com.lss.mapper.PhotoMapper;
import com.lss.service.PhotoAlbumService;
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
 * @create 2022年03月12日 21:25
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Resource
    UserService userService;
    @Resource
    PhotoMapper photoMapper;
    @Resource
    PhotoAlbumService albumService;

    @Override
    public Result<?> savePhotos(Integer albumId, List<String> photoList, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getPhotoResult(null, PhotoEnum.NOT_ABILITY_UPDATE);
        }
        try {
            for (String url : photoList) {
                Photo photo = new Photo();
                photo.setAlbumId(albumId);
                photo.setPhotoSrc(url);
                photo.setCreateTime(new Date());
                photo.setPhotoName(IdWorker.getIdStr());
                this.save(photo);
            }
        } catch (Exception e) {
            return Result.getPhotoResult(null, PhotoEnum.SAVE_PHOTO_ERROR);
        }
        return Result.getPhotoResult(null, PhotoEnum.SAVE_PHOTO_SUCCESS);
    }

    @Override
    public Result<JSONObject> listPhotos(Integer currentPage, Integer pageSize, Integer albumId, Integer isDelete) {
        currentPage = (currentPage - 1) * pageSize;
        JSONObject jsonResult = new JSONObject();
        List<Photo> photos = photoMapper.listPhotos(currentPage, pageSize, albumId, isDelete);
        jsonResult.put("data", photos);
        QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("album_id", albumId);
        queryWrapper.eq("is_delete", isDelete);
        long count = this.count(queryWrapper);
        jsonResult.put("count", count);
        return Result.getPhotoResult(jsonResult, PhotoEnum.LIST_PHOTO_SUCCESS);
    }

    @Override
    public Result<?> deletePhotos(List<Integer> deletePhotoIdList, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getPhotoResult(null, PhotoEnum.NOT_ABILITY_UPDATE);
        }
        Photo photo = this.getById(deletePhotoIdList.get(0));
        //进入回收站
        if (photo.getIsDelete() == 0) {
            for (Integer id : deletePhotoIdList) {
                UpdateWrapper<Photo> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", id);
                updateWrapper.set("is_delete", 1);
                this.update(updateWrapper);
            }
        } else {
            this.removeBatchByIds(deletePhotoIdList);
        }
        return Result.getPhotoResult(null, PhotoEnum.DELETE_PHOTO_SUCCESS);
    }

    @Override
    public Result<?> updatePhotos(Integer albumId, List<Integer> selectPhotoIdList, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getPhotoResult(null, PhotoEnum.NOT_ABILITY_UPDATE);
        }
        try {
            for (Integer id : selectPhotoIdList) {
                UpdateWrapper<Photo> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", id);
                updateWrapper.set("album_id", albumId);
                updateWrapper.set("is_delete", 0);
                this.update(updateWrapper);
            }
        } catch (Exception e) {
            return Result.getPhotoResult(null, PhotoEnum.MOVE_PHOTO_ERROR);
        }
        return Result.getPhotoResult(null, PhotoEnum.MOVE_PHOTO_SUCCESS);
    }

    @Override
    public Result<?> updatePhoto(Photo photo, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getPhotoResult(null, PhotoEnum.NOT_ABILITY_UPDATE);
        }
        UpdateWrapper<Photo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", photo.getId());
        updateWrapper.set("photo_name", photo.getPhotoName());
        updateWrapper.set("photo_desc", photo.getPhotoDesc());
        updateWrapper.set("update_time", new Date());
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getPhotoResult(null, PhotoEnum.UPDATE_PHOTO_ERROR);
        }
        return Result.getPhotoResult(null, PhotoEnum.UPDATE_PHOTO_SUCCESS);
    }

    @Override
    public Result<JSONObject> blogListPhotos(Integer current, Integer size, Integer albumId) {
        Result<JSONObject> listPhotos = this.listPhotos(current, size, albumId, 0);
        PhotoAlbum album = albumService.getById(albumId);
        listPhotos.getData().put("album", album);
        return listPhotos;
    }
}
