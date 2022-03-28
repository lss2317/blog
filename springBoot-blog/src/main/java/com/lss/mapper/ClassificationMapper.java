package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.Classification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类交互层
 *
 * @author lss
 * @create 2022年02月15日 14:50
 */
public interface ClassificationMapper extends BaseMapper<Classification> {

    /**
     * 分页查询
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     */
    List<Classification> listClassification(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage);

    /**
     * 根据关键字查询结果
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     * @param keywords    分类名关键词
     */
    List<Classification> listClassificationByKeywords(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage, @Param("keywords") String keywords);

    /**
     * 分类总列表
     *
     * @return 分类总List集合数据
     */
    List<Classification> listClassifications();
}
