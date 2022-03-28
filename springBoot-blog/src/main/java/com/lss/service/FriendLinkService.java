package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.FriendLink;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 友链服务类
 *
 * @author lss
 * @create 2022年03月16日 19:55
 */
public interface FriendLinkService extends IService<FriendLink> {

    /**
     * 添加或修改友链
     *
     * @param friendLink 友链实体类
     * @param request    请求
     */
    Result<?> addOrUpdateLink(FriendLink friendLink, HttpServletRequest request);

    /**
     * 查询友链集合
     *
     * @param currentPage 页码
     * @param pageSize    每页总数
     * @param keywords    友链名称关键词
     */
    Result<JSONObject> listFriendLink(Integer currentPage, Integer pageSize, String keywords);

    /**
     * 删除友链
     *
     * @param id      友链id
     * @param request 请求
     */
    Result<?> deleteLink(@Param("id") Integer id, HttpServletRequest request);

    /**
     * 批量删除友链
     *
     * @param linkIdList 删除友链id集合
     * @param request    请求
     */
    Result<?> deleteLinks(List<Integer> linkIdList, HttpServletRequest request);

    /**
     * 前台获取友链
     *
     * @return 友链List集合
     */
    List<FriendLink> listLink();

}
