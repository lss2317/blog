package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.User;
import com.lss.enums.UserEnum;
import com.lss.mapper.UserMapper;
import com.lss.service.RedisService;
import com.lss.service.UserService;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author lss
 * @create 2022年02月27日 19:59
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;
    @Resource
    RedisService redisService;
    @Resource
    HttpServletRequest request;

    @Override
    public List<User> listUsers(Integer currentPage, Integer pageSize, Integer loginType, String keywords) {
        currentPage = (currentPage - 1) * pageSize;
        return userMapper.listUsers(currentPage, pageSize, loginType, keywords);
    }

    @Override
    public Result<?> login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User select = this.getOne(queryWrapper);
        if (select == null) {
            return Result.getUserResult(null, UserEnum.LOGIN_ERROR);
        }
        //查看是否被禁用
        if (select.getIsDisable() == 1) {
            return Result.getUserResult(null, UserEnum.DISABLE_LOGIN);
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("last_login_time", new Date());
        updateWrapper.eq("id", select.getId());
        this.update(updateWrapper);
        JSONObject json = new JSONObject();
        String token = JWTUtils.createToken(select.getId() + "", select.getUsername());
        String uuid = UUID.randomUUID().toString();
        json.put("token", token);
        json.put("uuid", uuid);
        User tempUser = new User();
        tempUser.setAvatar(select.getAvatar());
        tempUser.setNickname(select.getNickname());
        json.put("user", tempUser);
        //Redis储存token
        redisService.set(RedisPrefixConst.PREFIX_TOKEN + uuid, token, 60 * 60);
        return Result.getUserResult(json, UserEnum.LOGIN_SUCCESS);
    }

    @Override
    public Result<?> updateRole(User user, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User userRole = this.getById((Serializable) userId);
        if (userRole.getRole() != 2) {
            return Result.getUserResult(null, UserEnum.NOT_ABILITY_UPDATE);
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("nickname", user.getNickname());
        updateWrapper.set("role", user.getRole());
        updateWrapper.eq("id", user.getId());
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getUserResult(null, UserEnum.UPDATE_ROLE_ERROR);
        }
        return Result.getUserResult(null, UserEnum.UPDATE_ROLE_SUCCESS);
    }

    @Override
    public Result<?> changeDisable(Integer id, Integer isDisable, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = this.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getUserResult(null, UserEnum.NOT_ABILITY_UPDATE);
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_disable", isDisable);
        updateWrapper.eq("id", id);
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getUserResult(null, UserEnum.UPDATE_ROLE_ERROR);
        }
        return Result.getUserResult(null, UserEnum.UPDATE_ROLE_SUCCESS);
    }

    @Override
    public Result<?> getSelf() {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = this.getById((Serializable) userId);
        User resultUser = new User();
        resultUser.setNickname(user.getNickname());
        resultUser.setIntro(user.getIntro());
        resultUser.setWebsite(user.getWebsite());
        resultUser.setAvatar(user.getAvatar());
        return Result.getUserResult(resultUser, UserEnum.LIST_USER_SUCCESS);
    }

    @Override
    public Result<?> updateInfo(User user) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.set("nickname", user.getNickname());
        updateWrapper.set("website", user.getWebsite());
        updateWrapper.set("intro", user.getIntro());
        this.update(updateWrapper);
        return Result.getUserResult(null, UserEnum.UPDATE_ROLE_SUCCESS);
    }

    @Override
    public Result<?> updatePassword(String oldPassword, String newPassword) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = this.getById((Serializable) userId);
        if (!user.getPassword().equals(oldPassword)) {
            return Result.getUserResult(null, UserEnum.PASSWORD_ERROR);
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password", newPassword);
        updateWrapper.eq("id", user.getId());
        this.update(updateWrapper);
        return Result.getUserResult(null, UserEnum.UPDATE_ROLE_SUCCESS);
    }

    @Override
    public Result<?> blogLogin(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User select = this.getOne(queryWrapper);
        if (select == null) {
            return Result.getUserResult(null, UserEnum.LOGIN_ERROR);
        }
        //查看是否被禁用
        if (select.getIsDisable() == 1) {
            return Result.getUserResult(null, UserEnum.DISABLE_LOGIN);
        }
        String token = JWTUtils.createToken(select.getId().toString(), select.getUsername());
        select.setIpAddress(token);
        select.setIpSource(token);
        //获取点赞说说id的set集合
        Set<Object> setTalkLikes = redisService.sMembers(RedisPrefixConst.TALK_USER_LIKE + select.getId());
        select.setTalkLikeSet(setTalkLikes);
        //获取点赞评论id的set集合
        Set<Object> setCommentLikes = redisService.sMembers(RedisPrefixConst.COMMENT_USER_LIKE + select.getId());
        select.setCommentLikeSet(setCommentLikes);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        updateWrapper.set("last_login_time", new Date());
        this.update(updateWrapper);
        return Result.getUserResult(select, UserEnum.LOGIN_SUCCESS);
    }

    @Override
    public Result<?> changeAvatar(User user) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object id = claims.get("id");
        updateWrapper.eq("id", id);
        updateWrapper.set("avatar", user.getAvatar());
        boolean update = this.update(updateWrapper);
        if (!update) {
            return Result.getUserResult(null, UserEnum.CHANGE_AVATAR_ERROR);
        }
        return Result.getUserResult(null, UserEnum.CHANGE_AVATAR_SUCCESS);
    }
}
