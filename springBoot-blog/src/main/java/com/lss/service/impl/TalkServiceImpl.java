package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.Comment;
import com.lss.entity.Talk;
import com.lss.entity.User;
import com.lss.enums.TalkEnum;
import com.lss.mapper.TalkMapper;
import com.lss.service.CommentService;
import com.lss.service.RedisService;
import com.lss.service.TalkService;
import com.lss.service.UserService;
import com.lss.utils.HTMLUtils;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lss
 * @create 2022年03月10日 21:01
 */
@Service
public class TalkServiceImpl extends ServiceImpl<TalkMapper, Talk> implements TalkService {

    @Resource
    TalkMapper talkMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Resource
    HttpServletRequest request;
    @Resource
    CommentService commentService;

    @Override
    public List<String> listHomeTalks() {
        //获取最新10条说说
        return this.list(new LambdaQueryWrapper<Talk>()
                .eq(Talk::getStatus, 1)
                .orderByDesc(Talk::getIsTop)
                .orderByDesc(Talk::getId)
                .last("limit 10"))
                .stream()
                .map(item -> item.getContent().length() > 200 ? HTMLUtils.deleteTag(item.getContent().substring(0, 200)) : HTMLUtils.deleteTag(item.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Talk> listTalks(Integer currentPage, Integer pageSize, Integer status) {
        currentPage = (currentPage - 1) * pageSize;
        List<Talk> talks = talkMapper.listTalks(currentPage, pageSize, status);
        for (Talk talk : talks) {
            if (talk.getImages() != null && !talk.getImages().equals("")) {
                List list = JSONObject.parseObject(talk.getImages(), List.class);
                talk.setImgList(list);
            }
        }
        return talks;
    }

    @Override
    public Result<?> saveOrUpdateTalk(Talk talk, HttpServletRequest request) {
        //获取用户id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getTalkResult(null, TalkEnum.NOT_ABILITY_UPDATE);
        }
        //修改说说
        if (talk.getId() != null) {
            talk.setUpdateTime(new Date());
            UpdateWrapper<Talk> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", talk.getId());
            updateWrapper.set("content", talk.getContent());
            updateWrapper.set("images", talk.getImages());
            updateWrapper.set("is_top", talk.getIsTop());
            updateWrapper.set("status", talk.getStatus());
            updateWrapper.set("update_time", new Date());
            boolean update = this.update(updateWrapper);
            if (!update) {
                return Result.getTalkResult(null, TalkEnum.UPDATE_TALK_ERROR);
            }
            return Result.getTalkResult(null, TalkEnum.UPDATE_TALK_SUCCESS);
        }
        //添加说说
        talk.setUserId(Integer.parseInt(String.valueOf(userId)));
        talk.setCreateTime(new Date());
        int insert = talkMapper.insert(talk);
        if (insert == 0) {
            return Result.getTalkResult(null, TalkEnum.SAVE_TALK_ERROR);
        }
        return Result.getTalkResult(null, TalkEnum.SAVE_TALK_SUCCESS);
    }

    @Override
    public Result<?> deleteTalk(Integer id, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getTalkResult(null, TalkEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeById(id);
        if (!remove) {
            return Result.getTalkResult(null, TalkEnum.DELETE_TALK_ERROR);
        }
        return Result.getTalkResult(null, TalkEnum.DELETE_TALK_SUCCESS);
    }

    @Override
    public Result<JSONObject> blogTalks(Integer current, Integer size) {
        JSONObject json = new JSONObject();
        current = (current - 1) * size;
        //获取说说
        List<Talk> talkList = talkMapper.listTalks(current, size, 1);
        List<Talk> list = new ArrayList<>();
        for (Talk talk : talkList) {
            if (talk.getIsTop() == 1) {
                if (talk.getImages() != null && !talk.getImages().equals("")) {
                    List tempList = JSONObject.parseObject(talk.getImages(), List.class);
                    talk.setImgList(tempList);
                }
                //从redis获取说说点赞量
                Object likeCount = redisService.hGet(RedisPrefixConst.TALK_LIKE_COUNT, talk.getId().toString());
                if (likeCount != null) {
                    talk.setLikeCount(Integer.parseInt(likeCount.toString()));
                }
                QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("talk_id", talk.getId());
                queryWrapper.eq("is_review", 0);
                long count = commentService.count(queryWrapper);
                talk.setCommentCount((int) count);
                list.add(talk);
            }
        }
        for (Talk talk : talkList) {
            if (talk.getIsTop() != 1) {
                if (talk.getImages() != null && !talk.getImages().equals("")) {
                    List tempList = JSONObject.parseObject(talk.getImages(), List.class);
                    talk.setImgList(tempList);
                }
                //从redis获取说说点赞量
                Object likeCount = redisService.hGet(RedisPrefixConst.TALK_LIKE_COUNT, talk.getId().toString());
                if (likeCount != null) {
                    talk.setLikeCount(Integer.parseInt(likeCount.toString()));
                }
                QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("talk_id", talk.getId());
                queryWrapper.eq("is_review", 0);
                long count = commentService.count(queryWrapper);
                talk.setCommentCount((int) count);
                list.add(talk);
            }
        }
        json.put("data", list);
        QueryWrapper<Talk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        long count = this.count(queryWrapper);
        json.put("count", count);
        return Result.getTalkResult(json, TalkEnum.SELECT_TALK_SUCCESS);
    }

    @Override
    public void saveTalkLike(Integer talkId) {
        //获取用户id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        // 判断是否点赞
        String talkLikeKey = RedisPrefixConst.TALK_USER_LIKE + userId;
        if (redisService.sIsMember(talkLikeKey, talkId)) {
            // 点过赞则删除说说id
            redisService.sRemove(talkLikeKey, talkId);
            // 说说点赞量-1
            redisService.hDecr(RedisPrefixConst.TALK_LIKE_COUNT, talkId.toString(), 1L);
        } else {
            // 未点赞则增加说说id
            redisService.sAdd(talkLikeKey, talkId);
            // 说说点赞量+1
            redisService.hIncr(RedisPrefixConst.TALK_LIKE_COUNT, talkId.toString(), 1L);
        }
    }

    @Override
    public Talk getOneTalk(Integer talkId) {
        Talk talk = this.getById(talkId);
        List list = JSONObject.parseObject(talk.getImages(), List.class);
        talk.setImgList(list);
        User user = userService.getById(talk.getUserId());
        talk.setAvatar(user.getAvatar());
        talk.setNickname(user.getNickname());
        //从redis获取说说点赞量
        Object likeCount = redisService.hGet(RedisPrefixConst.TALK_LIKE_COUNT, talk.getId().toString());
        if (likeCount != null) {
            talk.setLikeCount(Integer.parseInt(likeCount.toString()));
        }
        return talk;
    }
}
