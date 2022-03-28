package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.Comment;
import com.lss.enums.CommentEnum;
import com.lss.mapper.CommentMapper;
import com.lss.service.BlogInfoService;
import com.lss.service.CommentService;
import com.lss.service.RedisService;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lss
 * @create 2022年03月26日 15:26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    CommentMapper commentMapper;
    @Resource
    BlogInfoService blogInfoService;
    @Resource
    RedisService redisService;
    @Resource
    HttpServletRequest request;


    @Override
    public Result<?> saveComment(Comment comment) {
        // 判断是否需要审核
        Integer review = blogInfoService.getWebsiteConfig().getIsCommentReview();
        comment.setIsReview(review);
        comment.setCreateTime(new Date());
        boolean save = this.save(comment);
        if (!save) {
            return Result.getCommentResult(null, CommentEnum.SAVE_COMMENT_ERROR);
        }
        return Result.getCommentResult(null, CommentEnum.SAVE_COMMENT_SUCCESS);
    }

    @Override
    public Result<?> listComments(Integer current, Integer size, Integer type, Integer articleId, Integer talkId) {
        current = (current - 1) * size;
        JSONObject json = new JSONObject();
        // 查询评论量
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        //判断获取评论类型
        if (type.equals(1)) {
            commentQueryWrapper.eq("article_id", articleId);
        } else if (type.equals(3)) {
            commentQueryWrapper.eq("talk_id", talkId);
        } else {
            commentQueryWrapper.eq("type", type);
        }
        long commentTotal = this.count(commentQueryWrapper);
        commentQueryWrapper.isNull("parent_id");
        long commentCount = this.count(commentQueryWrapper);
        json.put("commentCount", commentCount);
        json.put("commentTotal", commentTotal);
        List<Comment> comments = commentMapper.listComments(current, size, type, articleId, talkId);
        if (comments.isEmpty()) {
            return Result.getCommentResult(json, CommentEnum.LIST_COMMENT_SUCCESS);
        }
        // 提取评论id集合
        List<Integer> commentIdList = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
        // 查询redis的评论点赞数据
        Map<String, Object> likeCountMap = redisService.hGetAll(RedisPrefixConst.COMMENT_LIKE_COUNT);
        //封装评论数据
        comments.forEach(item -> {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id", item.getId());
            queryWrapper.eq("is_review", 0);
            long count = this.count(queryWrapper);
            item.setReplyCount((int) count);
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
            //评论下面的评论
            List<Comment> replies = commentMapper.listRepliesByCommentId(0, 3, item.getId());
            replies.forEach(res -> {
                res.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
            });
            item.setReplyDTOList(replies);
        });

        json.put("data", comments);
        return Result.getCommentResult(json, CommentEnum.LIST_COMMENT_SUCCESS);
    }

    @Override
    public Result<List<Comment>> listRepliesByCommentId(Integer current, Integer size, Integer commentId) {
        current = (current - 1) * size;
        List<Comment> commentList = commentMapper.listRepliesByCommentId(current, size, commentId);
        // 查询redis的评论点赞数据
        Map<String, Object> likeCountMap = redisService.hGetAll(RedisPrefixConst.COMMENT_LIKE_COUNT);
        commentList.forEach(item -> {
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
        });
        return Result.getCommentResult(commentList, CommentEnum.LIST_COMMENT_SUCCESS);
    }

    @Override
    public void saveCommentLike(Integer commentId) {
        //获取用户id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        // 判断是否点赞
        String commentLikeKey = RedisPrefixConst.COMMENT_USER_LIKE + userId;
        if (redisService.sIsMember(commentLikeKey, commentId)) {
            // 点过赞则删除评论id
            redisService.sRemove(commentLikeKey, commentId);
            // 评论点赞量-1
            redisService.hDecr(RedisPrefixConst.COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        } else {
            // 未点赞则增加评论id
            redisService.sAdd(commentLikeKey, commentId);
            // 评论点赞量+1
            redisService.hIncr(RedisPrefixConst.COMMENT_LIKE_COUNT, commentId.toString(), 1L);
        }
    }

    @Override
    public Result<JSONObject> listAdminComment(Integer currentPage, Integer pageSize, Integer type, Integer isReview, String keywords) {
        if (keywords == null) {
            keywords = "";
        }
        JSONObject json = new JSONObject();
        currentPage = (currentPage - 1) * pageSize;
        List<Comment> commentList = commentMapper.listAdminComment(currentPage, pageSize, type, isReview, keywords.trim());
        json.put("data", commentList);
        Integer count = commentMapper.listCount(type, isReview, keywords.trim());
        json.put("count", count);
        return Result.getCommentResult(json, CommentEnum.LIST_COMMENT_SUCCESS);
    }
}
