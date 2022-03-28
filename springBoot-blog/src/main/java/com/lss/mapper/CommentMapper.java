package com.lss.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.common.Result;
import com.lss.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论交互层
 *
 * @author lss
 * @create 2022年03月26日 15:24
 */
public interface CommentMapper extends BaseMapper<Comment> {

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
    List<Comment> listComments(Integer current, Integer size, Integer type, Integer articleId, Integer talkId);

    /**
     * 查看当条评论下的回复
     *
     * @param commentId 评论id
     * @param current   当前页码
     * @param size      大小
     * @return 回复集合
     */
    List<Comment> listRepliesByCommentId(@Param("current") Integer current, @Param("size") Integer size, @Param("commentId") Integer commentId);

    /**
     * 后台分页获取评论列表
     *
     * @param currentPage 页码
     * @param pageSize    每页展示总数
     * @param type        评论类型
     * @param isReview    是否已经审核
     * @param keywords    评论人昵称关键词
     * @return List数据集合
     */
    List<Comment> listAdminComment(Integer currentPage, Integer pageSize, Integer type, Integer isReview, String keywords);

    /**
     * 按照条件查询评论总条数
     *
     * @param type     评论类型
     * @param isReview 是否已经审核
     * @param keywords 评论人昵称关键词
     * @return 总条数数据
     */
    Integer listCount(Integer type, Integer isReview, String keywords);

}
