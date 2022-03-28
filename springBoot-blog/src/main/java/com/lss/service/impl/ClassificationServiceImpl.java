package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.Classification;
import com.lss.entity.User;
import com.lss.enums.ClassificationEnum;
import com.lss.mapper.ClassificationMapper;
import com.lss.service.ClassificationService;
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
 * @create 2022年02月15日 14:52
 */
@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {

    @Resource
    ClassificationMapper classificationMapper;
    @Resource
    UserService userService;

    @Override
    public List<Classification> listClassification(Integer pageSize, Integer currentPage) {
        return classificationMapper.listClassification(pageSize, currentPage);
    }

    @Override
    public List<Classification> listClassificationByKeywords(Integer pageSize, Integer currentPage, String keywords) {
        return classificationMapper.listClassificationByKeywords(pageSize, currentPage, keywords);
    }

    @Override
    public Result<?> addClassification(Classification classification, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getClassificationResult(null, ClassificationEnum.NOT_ABILITY_UPDATE);
        }
        //查询分类名称是否已经存在
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("classification_name", classification.getClassificationName());
        Classification repeat = this.getOne(queryWrapper);
        if (repeat != null) {
            return Result.getClassificationResult(null, ClassificationEnum.REPEAT_CLASSIFICATION);
        }
        classification.setCreateTime(new Date());
        boolean save = this.save(classification);
        if (!save) {
            return Result.getClassificationResult(null, ClassificationEnum.ADD_CLASSIFICATION_ERROR);
        }
        return Result.getClassificationResult(null, ClassificationEnum.ADD_CLASSIFICATION_SUCCESS);
    }

    @Override
    public Result<?> modifyClassification(Classification classification, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getClassificationResult(null, ClassificationEnum.NOT_ABILITY_UPDATE);
        }
        //查询分类名称是否已经存在
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("classification_name", classification.getClassificationName());
        Classification repeat = this.getOne(queryWrapper);
        if (repeat != null) {
            return Result.getClassificationResult(null, ClassificationEnum.REPEAT_CLASSIFICATION);
        }
        UpdateWrapper<Classification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", classification.getId());
        updateWrapper.set("classification_name", classification.getClassificationName());
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getClassificationResult(null, ClassificationEnum.MODIFY_CLASSIFICATION_ERROR);
        }
        return Result.getClassificationResult(null, ClassificationEnum.MODIFY_CLASSIFICATION_SUCCESS);
    }

    @Override
    public Result<?> deleteClassification(Integer id, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getClassificationResult(null, ClassificationEnum.NOT_ABILITY_UPDATE);
        }
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        boolean remove = this.remove(queryWrapper);
        if (!remove) {
            return Result.getClassificationResult(null, ClassificationEnum.DELETE_CLASSIFICATION_ERROR);
        }
        return Result.getClassificationResult(null, ClassificationEnum.DELETE_CLASSIFICATION_SUCCESS);
    }

    @Override
    public Result<?> deleteClassifications(List<Integer> classIdList, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getClassificationResult(null, ClassificationEnum.NOT_ABILITY_UPDATE);
        }
        boolean delete = this.removeByIds(classIdList);
        if (!delete) {
            return Result.getClassificationResult(null, ClassificationEnum.DELETE_CLASSIFICATION_ERROR);
        }
        return Result.getClassificationResult(null, ClassificationEnum.DELETE_CLASSIFICATION_SUCCESS);
    }

    @Override
    public Result<JSONObject> listClassifications() {
        List<Classification> classificationList = classificationMapper.listClassifications();
        long count = this.count();
        JSONObject json = new JSONObject();
        json.put("data", classificationList);
        json.put("count", count);
        return Result.getClassificationResult(json, ClassificationEnum.SEARCH_CLASSIFICATION_SUCCESS);
    }
}
