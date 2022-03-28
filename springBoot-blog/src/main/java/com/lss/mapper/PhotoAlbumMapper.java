package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.PhotoAlbum;

import java.util.List;

/**
 * 相册交互层
 *
 * @author lss
 * @create 2022年03月12日 21:19
 */
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {

    /**
     * 分页查询相册列表
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     * @param keywords    相册名关键词
     */
    List<PhotoAlbum> listAlbum(Integer pageSize, Integer currentPage, String keywords);
}
