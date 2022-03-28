package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.OperationLog;

import java.util.List;

/**
 * 日志服务
 *
 * @author lss
 * @create 2022年03月18日 22:43
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 查询日志列表
     *
     * @param currentPage 页面
     * @param pageSize    每页总数
     * @param keywords    搜索关键词
     * @return 日志列表
     */
    Result<JSONObject> listLogs(Integer currentPage, Integer pageSize, String keywords);

    /**
     * 删除日志
     *
     * @param id 日志id
     * @return 反馈信息
     */
    Result<?> deleteLog(Integer id);

    /**
     * 批量删除日志
     *
     * @param logIdList 删除日志id集合
     * @return 反馈信息
     */
    Result<?> deleteLogs(List<Integer> logIdList);
}
