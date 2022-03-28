package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.OperationLog;
import com.lss.entity.User;
import com.lss.enums.LogEnum;
import com.lss.mapper.OperationLogMapper;
import com.lss.service.OperationLogService;
import com.lss.service.UserService;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * @author lss
 * @create 2022年03月18日 22:44
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Resource
    OperationLogMapper operationLogMapper;
    @Resource
    HttpServletRequest request;
    @Resource
    UserService userService;

    @Override
    public Result<JSONObject> listLogs(Integer currentPage, Integer pageSize, String keywords) {
        currentPage = (currentPage - 1) * pageSize;
        JSONObject json = new JSONObject();
        List<OperationLog> logs = operationLogMapper.listLogs(currentPage, pageSize, keywords.trim());
        json.put("data", logs);
        Integer count = operationLogMapper.count(currentPage, pageSize, keywords.trim());
        json.put("count", count);
        return Result.getLogResult(json, LogEnum.LIST_LOGS_SUCCESS);
    }

    @Override
    public Result<?> deleteLog(Integer id) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getLogResult(null, LogEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeById(id);
        if (!remove) {
            return Result.getLogResult(null, LogEnum.DELETE_LOGS_ERROR);
        }
        return Result.getLogResult(null, LogEnum.DELETE_LOGS_SUCCESS);
    }

    @Override
    public Result<?> deleteLogs(List<Integer> logIdList) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getLogResult(null, LogEnum.NOT_ABILITY_UPDATE);
        }
        boolean removes = this.removeBatchByIds(logIdList);
        if (!removes) {
            return Result.getLogResult(null, LogEnum.DELETE_LOGS_ERROR);
        }
        return Result.getLogResult(null, LogEnum.DELETE_LOGS_SUCCESS);
    }
}
