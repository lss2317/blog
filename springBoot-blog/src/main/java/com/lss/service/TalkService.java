package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Talk;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 说说服务类
 *
 * @author lss
 * @create 2022年03月10日 21:01
 */
public interface TalkService extends IService<Talk> {

    /**
     * 获取说说列表，按照发布时间从晚到早
     *
     * @param currentPage 页码
     * @param pageSize    每页展示个数
     * @param status      状态 1.公开、2.私有
     */
    List<Talk> listTalks(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("status") Integer status);

    /**
     * 添加或修改说说
     *
     * @param talk    说说实体类
     * @param request 请求，获取token
     */
    Result<?> saveOrUpdateTalk(Talk talk, HttpServletRequest request);

    /**
     * 删除说说
     *
     * @param id      说说id
     * @param request 请求，获取token
     */
    Result<?> deleteTalk(Integer id, HttpServletRequest request);

    /**
     * 前台获取说说列表
     *
     * @param current 页码
     * @param size    每次展示个数
     * @return 说说数据反馈
     */
    Result<JSONObject> blogTalks(Integer current, Integer size);

    /**
     * 点赞说说或取消点赞
     *
     * @param talkId 说说id
     */
    void saveTalkLike(Integer talkId);

    /**
     * 获取单条说说
     */
    Talk getOneTalk(Integer talkId);
}
