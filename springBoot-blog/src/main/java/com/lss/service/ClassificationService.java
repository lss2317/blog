package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Classification;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类服务类
 *
 * @author lss
 * @create 2022年02月15日 14:52
 */
public interface ClassificationService extends IService<Classification> {

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
     * 添加分类
     *
     * @param classification 分类实体类
     * @param request        请求，获取token
     */
    Result<?> addClassification(Classification classification, HttpServletRequest request);

    /**
     * 修改分类名称
     *
     * @param classification 分类实体类
     * @param request        请求，获取token
     */
    Result<?> modifyClassification(@RequestBody Classification classification, HttpServletRequest request);

    /**
     * 删除分类
     *
     * @param id      分类id
     * @param request 请求，获取token
     */
    Result<?> deleteClassification(@Param("id") Integer id, HttpServletRequest request);

    /**
     * 批量删除分类
     *
     * @param classIdList 删除分类id集合
     * @param request     请求，获取token
     */
    Result<?> deleteClassifications(@RequestBody List<Integer> classIdList, HttpServletRequest request);

    /**
     * 前台获取分类总列表
     *
     * @return 分类集合json数据
     */
    Result<JSONObject> listClassifications();
}
