package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.Photo;

import java.util.List;

/**
 * 照片交互层
 *
 * @author lss
 * @create 2022年03月12日 21:19
 */
public interface PhotoMapper extends BaseMapper<Photo> {

    /**
     * 获取相册的照片列表
     *
     * @param currentPage 页码
     * @param pageSize    每页展示个数
     * @param albumId     相册id
     * @param isDelete    是否删除 1、是 0、否
     */
    List<Photo> listPhotos(Integer currentPage, Integer pageSize, Integer albumId, Integer isDelete);
}
