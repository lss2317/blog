package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.FriendLink;

import java.util.List;

/**
 * 友链交互层
 *
 * @author lss
 * @create 2022年03月16日 19:53
 */
public interface FriendLinkMapper extends BaseMapper<FriendLink> {

    /**
     * 查询友链集合
     *
     * @param currentPage 页码
     * @param pageSize    每页总数
     * @param keywords    友链名称关键词
     */
    List<FriendLink> listFriendLink(Integer currentPage, Integer pageSize, String keywords);
}
