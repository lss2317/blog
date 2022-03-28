package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Message;

import java.util.List;

/**
 * 留言服务
 *
 * @author lss
 * @create 2022年03月21日 22:35
 */
public interface MessageService extends IService<Message> {

    /**
     * 添加保存留言
     *
     * @param message 留言实体类接收数据
     */
    void saveMessage(Message message);

    /**
     * 前台获取留言数据
     */
    Result<?> blogListMessage();

    /**
     * 后台分页搜索留言
     *
     * @param currentPage 页码
     * @param pageSize    每页展示条数
     * @param isReview    是否被审核过
     * @param keywords    搜索昵称关键词
     * @return json数据集合
     */
    Result<JSONObject> listMessage(Integer currentPage, Integer pageSize, Integer isReview, String keywords);

    /**
     * 恢复留言
     *
     * @param id 留言id
     * @return 反馈信息
     */
    Result<?> restoreMessage(Integer id);

    /**
     * 批量删除留言
     *
     * @param deleteIdList 删除留言id集合
     * @return 反馈前端信息
     */
    Result<?> deleteMessage(List<Integer> deleteIdList);

    /**
     * 批量审核通过留言
     *
     * @param checkMessage 审核留言id集合
     * @return 反馈前端信息
     */
    Result<?> checkMessages(List<Integer> checkMessage);
}
