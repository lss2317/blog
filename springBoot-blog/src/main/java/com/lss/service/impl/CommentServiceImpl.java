package com.lss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.MQPrefixConst;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.Comment;
import com.lss.entity.User;
import com.lss.enums.CommentEnum;
import com.lss.mapper.CommentMapper;
import com.lss.service.*;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Resource
    UserService userService;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    TalkService talkService;
    @Resource
    ArticleService articleService;

    @Value("${website.url}")
    String websiteUrl;


    @Transactional
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
        // 判断是否开启邮箱通知,通知用户
        if (blogInfoService.getWebsiteConfig().getIsEmailNotice() == 1) {
            notice(comment);
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

    @Transactional
    @Override
    public Result<?> checkComments(JSONObject json) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getCommentResult(null, CommentEnum.NOT_ABILITY_UPDATE);
        }
        //获取审核id集合
        String isReviewList = json.getString("isReviewList");
        List<Integer> list = JSONObject.parseObject(isReviewList, List.class);
        for (Integer id : list) {
            UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id);
            updateWrapper.set("is_review", 0);
            boolean update = this.update(updateWrapper);
            if (!update) {
                return Result.getCommentResult(null, CommentEnum.CHECK_COMMENT_ERROR);
            }
        }
        return Result.getCommentResult(null, CommentEnum.CHECK_COMMENT_SUCCESS);
    }

    @Transactional
    @Override
    public Result<?> deleteComments(List<Integer> deleteIdList) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getCommentResult(null, CommentEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeBatchByIds(deleteIdList);
        if (!remove) {
            return Result.getCommentResult(null, CommentEnum.DELETE_COMMENT_ERROR);
        }
        return Result.getCommentResult(null, CommentEnum.DELETE_COMMENT_SUCCESS);
    }

    /**
     * 通知评论用户
     *
     * @param comment 评论信息
     */
    public void notice(Comment comment) {
        // 查询回复用户邮箱号
        Integer userId = 1;
        String id = "";
        String url = websiteUrl + "/links";
        switch (Objects.requireNonNull(comment.getType())) {
            case 1:
                userId = articleService.getById(comment.getArticleId()).getUserId();
                id = comment.getArticleId().toString();
                url = websiteUrl + "/article/" + id;
                break;
            case 3:
                userId = talkService.getById(comment.getTalkId()).getUserId();
                id = comment.getTalkId().toString();
                url = websiteUrl + "/talks/" + id;
                break;
            default:
                break;
        }
        if (Objects.nonNull(comment.getReplyUserId())) {
            userId = comment.getReplyUserId();
        }
        String email = userService.getById(userId).getEmail();
        if (StringUtils.isNotBlank(email)) {
            // 发送消息
            JSONObject json = new JSONObject();
            if (comment.getIsReview().equals(0)) {
                // 评论提醒
                json.put("email", email);
                json.put("subject", "评论提醒");
                // 获取评论路径
                json.put("content", "您收到了一条新的回复，请前往" + url + "\n页面查看");
            } else {
                // 管理员审核提醒
                String adminEmail = userService.getById(1).getEmail();
                json.put("email", adminEmail);
                json.put("subject", "审核提醒");
                json.put("content", "您收到了一条新的回复，请前往后台管理页面审核");
            }
            rabbitTemplate.convertAndSend(MQPrefixConst.EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(json), new MessageProperties()));
        }
    }
}
