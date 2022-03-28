package com.lss.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.FriendLink;
import com.lss.entity.User;
import com.lss.enums.FriendLinkEnum;
import com.lss.mapper.FriendLinkMapper;
import com.lss.service.FriendLinkService;
import com.lss.service.UserService;
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
 * @create 2022年03月16日 19:55
 */
@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Resource
    UserService userService;
    @Resource
    FriendLinkMapper friendLinkMapper;

    @Override
    public Result<?> addOrUpdateLink(FriendLink friendLink, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.NOT_ABILITY_UPDATE);
        }
        //修改友链
        if (friendLink.getId() != null) {
            UpdateWrapper<FriendLink> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", friendLink.getId());
            updateWrapper.set("link_name", friendLink.getLinkName());
            updateWrapper.set("link_avatar", friendLink.getLinkAvatar());
            updateWrapper.set("link_address", friendLink.getLinkAddress());
            updateWrapper.set("link_intro", friendLink.getLinkIntro());
            updateWrapper.set("update_time", new Date());
            boolean update = this.update(updateWrapper);
            if (!update) {
                return Result.getFriendLinkResult(null, FriendLinkEnum.UPDATE_LINK_ERROR);
            }
            return Result.getFriendLinkResult(null, FriendLinkEnum.UPDATE_LINK_SUCCESS);
        }
        friendLink.setCreateTime(new Date());
        boolean save = this.save(friendLink);
        if (!save) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.SAVE_LINK_ERROR);
        }
        return Result.getFriendLinkResult(null, FriendLinkEnum.SAVE_LINK_SUCCESS);
    }

    @Override
    public Result<JSONObject> listFriendLink(Integer currentPage, Integer pageSize, String keywords) {
        JSONObject json = new JSONObject();
        currentPage = (currentPage - 1) * pageSize;
        List<FriendLink> friendLinkList = friendLinkMapper.listFriendLink(currentPage, pageSize, keywords.trim());
        json.put("data", friendLinkList);
        QueryWrapper<FriendLink> queryWrapper = new QueryWrapper<>();
        if (!keywords.trim().equals("")) {
            queryWrapper.like("link_name", keywords.trim());
        }
        long count = this.count(queryWrapper);
        json.put("count", count);
        return Result.getFriendLinkResult(json, FriendLinkEnum.LIST_LINK_SUCCESS);
    }

    @Override
    public Result<?> deleteLink(Integer id, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeById(id);
        if (!remove) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.DELETE_LINK_ERROR);
        }
        return Result.getFriendLinkResult(null, FriendLinkEnum.DELETE_LINK_SUCCESS);
    }

    @Override
    public Result<?> deleteLinks(List<Integer> linkIdList, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.NOT_ABILITY_UPDATE);
        }
        boolean removes = this.removeBatchByIds(linkIdList);
        if (!removes) {
            return Result.getFriendLinkResult(null, FriendLinkEnum.DELETE_LINK_ERROR);
        }
        return Result.getFriendLinkResult(null, FriendLinkEnum.DELETE_LINK_SUCCESS);
    }

    @Override
    public List<FriendLink> listLink() {
        return this.list();
    }
}
