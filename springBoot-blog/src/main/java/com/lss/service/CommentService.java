package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Comment;

import java.util.List;

/**
 * 评论服务
 *
 * @author lss
 * @create 2022年03月26日 15:25
 */
public interface CommentService extends IService<Comment> {

    /**
     * 新增评论
     *
     * @param comment 评论实体类
     * @return 前端反馈
     */
    Result<?> saveComment(Comment comment);

    /**
     * 查询评论列表
     *
     * @param current   页码
     * @param size      每次展示条数
     * @param type      评论类型
     * @param articleId 文章id
     * @param talkId    说说id
     * @return json数据集合
     */
    Result<?> listComments(Integer current, Integer size, Integer type, Integer articleId, Integer talkId);

    /**
     * 获取评论下面的回复
     *
     * @param commentId 评论id
     * @return 数据
     */
    Result<List<Comment>> listRepliesByCommentId(Integer current, Integer size, Integer commentId);

    /**
     * 点赞评论
     *
     * @param commentId 评论id
     */
    void saveCommentLike(Integer commentId);

    /**
     * 后台获取评论列表
     *
     * @param currentPage 页码
     * @param pageSize    每页展示总数
     * @param type        评论类型
     * @param isReview    是否已经审核 0是 1否
     * @param keywords    评论人昵称关键词
     * @return json封装数据
     */
    Result<JSONObject> listAdminComment(Integer currentPage, Integer pageSize, Integer type, Integer isReview, String keywords);

    /**
     * 审核评论
     *
     * @param json json接收审核留言List集合
     */
    Result<?> checkComments(JSONObject json);

    /**
     * 删除评论
     *
     * @param deleteIdList 删除评论id集合
     */
    Result<?> deleteComments(List<Integer> deleteIdList);
}
