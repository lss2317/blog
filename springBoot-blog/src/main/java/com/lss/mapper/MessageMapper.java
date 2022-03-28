package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.Message;

import java.util.List;

/**
 * 留言交互
 *
 * @author lss
 * @create 2022年03月21日 22:34
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取不需要审核留言总数据给前台
     *
     * @return 留言List集合
     */
    List<Message> blogListMessage();

    /**
     * 后台分页搜索留言
     *
     * @param currentPage 页码
     * @param pageSize    每页展示条数
     * @param isReview    是否被审核过
     * @param keywords    搜索昵称关键词
     * @return List集合数据
     */
    List<Message> listMessage(Integer currentPage, Integer pageSize, Integer isReview, String keywords);
}
