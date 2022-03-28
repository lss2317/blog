package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Classification;
import com.lss.enums.ClassificationEnum;
import com.lss.service.ClassificationService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类控制器
 *
 * @author lss
 * @create 2022年02月15日 16:34
 */
@RestController
@RequestMapping("classification")
public class ClassificationController {

    @Resource
    ClassificationService classificationService;
    @Resource
    HttpServletRequest request;

    /**
     * 添加分类
     */
    @PostMapping("addClassification")
    @OptLog(ApiModule = "分类模块", ApiDesc = "新增分类", optType = OptTypeConst.SAVE)
    public Result<?> addClassification(@RequestBody Classification classification) {
        return classificationService.addClassification(classification, request);
    }

    /**
     * 修改分类名称
     */
    @PostMapping("modifyClassification")
    @OptLog(ApiModule = "分类模块", ApiDesc = "修改分类", optType = OptTypeConst.UPDATE)
    public Result<?> modifyClassification(@RequestBody Classification classification) {
        return classificationService.modifyClassification(classification, request);
    }


    /**
     * 通过搜索内容或者分页消息获取标签列表
     *
     * @param pageSize    每页显示条数
     * @param currentPage 页码
     * @param keywords    关键词
     */
    @GetMapping("listClassification")
    public Result<JSONObject> listClassification(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage, @Param("keywords") String keywords) {
        JSONObject json = new JSONObject();
        if (keywords == null || keywords.trim().equals("")) {
            long count = classificationService.count();
            json.put("count", count);
            currentPage = (currentPage - 1) * pageSize;
            List<Classification> classificationList = classificationService.listClassification(pageSize, currentPage);
            json.put("data", classificationList);
            return Result.getClassificationResult(json, ClassificationEnum.SEARCH_CLASSIFICATION_SUCCESS);
        }
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("classification_name", keywords.trim());
        long count = classificationService.count(queryWrapper);
        json.put("count", count);
        currentPage = (currentPage - 1) * pageSize;
        List<Classification> classificationList = classificationService.listClassificationByKeywords(pageSize, currentPage, keywords.trim());
        json.put("data", classificationList);
        return Result.getClassificationResult(json, ClassificationEnum.SEARCH_CLASSIFICATION_SUCCESS);
    }

    /**
     * 删除分类
     */
    @GetMapping("deleteClassification")
    @OptLog(ApiModule = "分类模块", ApiDesc = "删除分类", optType = OptTypeConst.REMOVE)
    public Result<?> deleteClassification(@Param("id") Integer id) {
        return classificationService.deleteClassification(id, request);
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("deleteClassifications")
    @OptLog(ApiModule = "分类模块", ApiDesc = "批量删除分类", optType = OptTypeConst.REMOVE)
    public Result<?> deleteClassifications(@RequestBody List<Integer> classIdList) {
        return classificationService.deleteClassifications(classIdList, request);
    }

    /**
     * 文章页分类列表
     */
    @GetMapping("search")
    public Result<List<Classification>> searchClassifications(@Param("keywords") String keywords) {
        if (keywords == null || keywords.equals("")) {
            List<Classification> list = classificationService.list();
            return Result.getClassificationResult(list, ClassificationEnum.SEARCH_CLASSIFICATION_SUCCESS);
        }
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("classification_name", keywords);
        List<Classification> list = classificationService.list(queryWrapper);
        return Result.getClassificationResult(list, ClassificationEnum.SEARCH_CLASSIFICATION_SUCCESS);
    }

    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<Classification>> listClassifications() {
        List<Classification> list = classificationService.list();
        return Result.getClassificationResult(list, ClassificationEnum.SEARCH_CLASSIFICATION_SUCCESS);
    }

    /**
     * 前台获取分类集合
     */
    @GetMapping("listClass")
    public Result<JSONObject> blogListClassifications() {
        return classificationService.listClassifications();
    }
}
