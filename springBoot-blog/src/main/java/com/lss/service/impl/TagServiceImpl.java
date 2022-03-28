package com.lss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.entity.Tag;
import com.lss.entity.User;
import com.lss.enums.TagEnum;
import com.lss.mapper.TagMapper;
import com.lss.service.TagService;
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
 * @create 2022年02月11日 15:58
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    TagMapper tagMapper;
    @Resource
    UserService userService;

    @Override
    public List<Tag> listTag(Integer pageSize, Integer currentPage) {
        return tagMapper.listTag(pageSize, currentPage);
    }

    @Override
    public List<Tag> listTagByTagName(Integer pageSize, Integer currentPage, String keywords) {
        return tagMapper.listTagByTagName(pageSize, currentPage, keywords);
    }

    @Override
    public Result<?> modifyTag(Tag tag, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getTagResult(null, TagEnum.NOT_ABILITY_UPDATE);
        }
        //判断标签是否已经存在
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_name", tag.getTagName());
        Tag repeatResult = this.getOne(queryWrapper);
        if (repeatResult != null) {
            return Result.getTagResult(null, TagEnum.REPEAT_TAG);
        }
        UpdateWrapper<Tag> tagUpdateWrapper = new UpdateWrapper<>();
        tagUpdateWrapper.eq("id", tag.getId());
        tagUpdateWrapper.set("tag_name", tag.getTagName());
        boolean update = this.update(tagUpdateWrapper);
        if (!update) {
            return Result.getTagResult(null, TagEnum.MODIFY_TAG_ERROR);
        }
        return Result.getTagResult(null, TagEnum.MODIFY_TAG_SUCCESS);
    }

    @Override
    public Result<?> addTag(Tag tag, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getTagResult(null, TagEnum.NOT_ABILITY_UPDATE);
        }
        //判断标签是否已经存在
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tag_name", tag.getTagName());
        Tag repeatResult = this.getOne(queryWrapper);
        if (repeatResult != null) {
            return Result.getTagResult(null, TagEnum.REPEAT_TAG);
        }
        tag.setCreateTime(new Date());
        boolean save = this.save(tag);
        if (!save) {
            return Result.getTagResult(null, TagEnum.ADD_TAG_ERROR);
        }
        return Result.getTagResult(null, TagEnum.ADD_TAG_SUCCESS);
    }

    @Override
    public Result<?> deleteTag(Integer id, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getTagResult(null, TagEnum.NOT_ABILITY_UPDATE);
        }
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        boolean remove = this.remove(queryWrapper);
        if (!remove) {
            return Result.getTagResult(null, TagEnum.DELETE_TAG_ERROR);
        }
        return Result.getTagResult(null, TagEnum.DELETE_TAG_SUCCESS);
    }

    @Override
    public Result<?> deleteTags(List<Integer> tagIdList, HttpServletRequest request) {
        //获取根据token获取id
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getTagResult(null, TagEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeByIds(tagIdList);
        if (!remove) {
            return Result.getTagResult(null, TagEnum.DELETE_TAG_ERROR);
        }
        return Result.getTagResult(null, TagEnum.DELETE_TAG_SUCCESS);
    }
}
