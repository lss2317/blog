package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.OperationLog;
import com.lss.service.OperationLogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 日志控制层
 *
 * @author lss
 * @create 2022年03月19日 14:59
 */
@RestController
@RequestMapping("log")
public class OperationLogController {

    @Resource
    OperationLogService logService;

    /**
     * 分页查询日志列表
     *
     * @return 日志结果集合
     */
    @GetMapping("listLogs")
    public Result<JSONObject> listLogs(@RequestParam Integer currentPage, @RequestParam Integer pageSize, String keywords) {
        //避免空指针异常
        if (keywords == null) {
            keywords = "";
        }
        return logService.listLogs(currentPage, pageSize, keywords);
    }

    /**
     * 删除日志
     *
     * @return 返回结果信息
     */
    @PostMapping("deleteLog")
    @OptLog(ApiModule = "日志模块", ApiDesc = "删除日志", optType = OptTypeConst.REMOVE)
    public Result<?> deleteLog(@RequestBody OperationLog log) {
        return logService.deleteLog(log.getId());
    }

    /**
     * 批量删除日志
     *
     * @return 返回结果信息
     */
    @DeleteMapping("deleteLogs")
    @OptLog(ApiModule = "日志模块", ApiDesc = "批量删除日志", optType = OptTypeConst.REMOVE)
    public Result<?> deleteLogs(@RequestBody List<Integer> logIdList) {
        return logService.deleteLogs(logIdList);
    }
}
