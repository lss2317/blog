package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Comment;
import com.lss.enums.CommentEnum;
import com.lss.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论控制层
 *
 * @author lss
 * @create 2022年03月26日 17:19
 */
@RestController
@RequestMapping("comment")
public class CommentController {

    @Resource
    CommentService commentService;

    /**
     * 添加评论
     *
     * @param comment 接收参数
     */
    @PostMapping("saveComment")
    public Result<?> saveComment(@RequestBody Comment comment) {
        return commentService.saveComment(comment);
    }

    /**
     * 获取回复下面的评论
     */
    @GetMapping("listComments")
    public Result<?> listComments(Integer current, Integer size, Integer type, Integer articleId, Integer talkId) {
        return commentService.listComments(current, size, type, articleId, talkId);
    }

    /**
     * 评论点赞
     *
     * @param commentId 评论id
     * @return {@link Result<>}
     */
    @PostMapping("/{commentId}/like")
    public Result<?> saveCommentLike(@PathVariable("commentId") Integer commentId) {
        commentService.saveCommentLike(commentId);
        return Result.getCommentResult(null, CommentEnum.LIST_COMMENT_SUCCESS);
    }

    /**
     * 查询评论下的回复
     *
     * @param commentId 评论id
     * @return {@link Result<Comment>} 回复列表
     */
    @GetMapping("/{commentId}/replies")
    public Result<List<Comment>> listRepliesByCommentId(@PathVariable("commentId") Integer commentId, Integer current, Integer size) {
        return commentService.listRepliesByCommentId(current, size, commentId);
    }

    /**
     * 后台分页查询评论列表
     *
     * @return json封装评论集合数据
     */
    @GetMapping("listAdminComment")
    public Result<JSONObject> listAdminComment(Integer currentPage, Integer pageSize, Integer type, Integer isReview, String keywords) {
        return commentService.listAdminComment(currentPage, pageSize, type, isReview, keywords);
    }

    /**
     * 审核评论
     *
     * @param jsonObject 接收参数
     */
    @PutMapping("checkComments")
    @OptLog(ApiModule = "评论模块", ApiDesc = "审核评论", optType = OptTypeConst.UPDATE)
    public Result<?> checkComments(@RequestBody JSONObject jsonObject) {
        return commentService.checkComments(jsonObject);
    }

    /**
     * 删除评论
     *
     * @param deleteIdList 删除评论id集合
     */
    @DeleteMapping("deleteComments")
    @OptLog(ApiModule = "评论模块", ApiDesc = "删除评论", optType = OptTypeConst.REMOVE)
    public Result<?> deleteComments(@RequestBody List<Integer> deleteIdList) {
        return commentService.deleteComments(deleteIdList);
    }
}
