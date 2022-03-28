package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签交互层
 *
 * @author lss
 * @create 2022年02月11日 15:55
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 分页查询
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     */
    List<Tag> listTag(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage);

    /**
     * 分页查询，根据关键词
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     * @param keywords    标签名关键词
     */
    List<Tag> listTagByTagName(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage, @Param("keywords") String keywords);

}
