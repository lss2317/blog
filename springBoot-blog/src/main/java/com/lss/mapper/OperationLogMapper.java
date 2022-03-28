package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.OperationLog;

import java.util.List;

/**
 * 操作日志
 *
 * @author lss
 * @create 2022年03月18日 22:42
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 查询日志列表
     *
     * @param currentPage 页面
     * @param pageSize    每页总数
     * @param keywords    搜索关键词
     * @return 日志列表
     */
    List<OperationLog> listLogs(Integer currentPage, Integer pageSize, String keywords);

    /**
     * 查询日志总条数
     *
     * @param currentPage 页面
     * @param pageSize    每页总数
     * @param keywords    搜索关键词
     * @return 日志列表
     */
    Integer count(Integer currentPage, Integer pageSize, String keywords);
}
