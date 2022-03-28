package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.Message;
import com.lss.entity.User;
import com.lss.entity.WebsiteConfig;
import com.lss.enums.MessageEnum;
import com.lss.mapper.MessageMapper;
import com.lss.service.BlogInfoService;
import com.lss.service.MessageService;
import com.lss.service.UserService;
import com.lss.utils.IpUtils;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lss
 * @create 2022年03月21日 22:35
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    HttpServletRequest request;
    @Resource
    BlogInfoService blogInfoService;
    @Resource
    MessageMapper messageMapper;
    @Resource
    UserService userService;

    @Override
    public void saveMessage(Message message) {
        //获取网站配置信息，查看是否审核留言
        WebsiteConfig websiteConfig = blogInfoService.getWebsiteConfig();
        Integer review = websiteConfig.getIsCommentReview();
        // 获取用户ip信息
        String ipAddress = IpUtils.getIpAddress(request);
        String ipSource = IpUtils.getIpSource(ipAddress);
        message.setCreateTime(new Date());
        message.setIpAddress(ipAddress);
        message.setIpSource(ipSource);
        message.setIsReview(review);
        this.save(message);
    }

    @Override
    public Result<?> blogListMessage() {
        List<Message> messageList = messageMapper.blogListMessage();
        return Result.getMessageResult(messageList, MessageEnum.LIST_MESSAGE_SUCCESS);
    }

    @Override
    public Result<JSONObject> listMessage(Integer currentPage, Integer pageSize, Integer isReview, String keywords) {
        //避免keywords是空，导致空指针异常
        if (keywords == null) {
            keywords = "";
        }
        currentPage = (currentPage - 1) * pageSize;
        JSONObject json = new JSONObject();
        List<Message> messageList = messageMapper.listMessage(currentPage, pageSize, isReview, keywords.trim());
        json.put("data", messageList);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        if (isReview != -1) {
            queryWrapper.eq("is_review", isReview);
        }
        if (!keywords.trim().equals("")) {
            queryWrapper.like("nickname", keywords.trim());
        }
        long count = this.count(queryWrapper);
        json.put("count", count);
        return Result.getMessageResult(json, MessageEnum.LIST_MESSAGE_SUCCESS);
    }

    @Override
    public Result<?> restoreMessage(Integer id) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getMessageResult(null, MessageEnum.NOT_ABILITY_UPDATE);
        }
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_review", 0);
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getMessageResult(null, MessageEnum.RESTORE_MESSAGE_ERROR);
        }
        return Result.getMessageResult(null, MessageEnum.RESTORE_MESSAGE_SUCCESS);
    }

    @Override
    public Result<?> deleteMessage(List<Integer> deleteIdList) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getMessageResult(null, MessageEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeBatchByIds(deleteIdList);
        if (!remove) {
            return Result.getMessageResult(null, MessageEnum.DELETE_MESSAGE_ERROR);
        }
        return Result.getMessageResult(null, MessageEnum.DELETE_MESSAGE_SUCCESS);
    }

    @Override
    public Result<?> checkMessages(List<Integer> checkMessage) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getMessageResult(null, MessageEnum.NOT_ABILITY_UPDATE);
        }
        for (Integer id : checkMessage) {
            UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id);
            updateWrapper.set("is_review", 0);
            boolean update = this.update(updateWrapper);
            if (!update) {
                throw new RuntimeException("事务没有完成");
            }
        }
        return Result.getMessageResult(null, MessageEnum.RESTORE_MESSAGE_SUCCESS);
    }
}
