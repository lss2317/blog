package com.lss.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.common.Result;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.Page;
import com.lss.entity.User;
import com.lss.enums.PageEnum;
import com.lss.mapper.PageMapper;
import com.lss.service.PageService;
import com.lss.service.RedisService;
import com.lss.service.UserService;
import com.lss.utils.BeanCopyUtils;
import com.lss.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author lss
 * @create 2022年03月17日 11:07
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {

    @Resource
    UserService userService;
    @Resource
    RedisService redisService;

    @Override
    public Result<?> saveOrUpdatePage(Page page, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getPageResult(null, PageEnum.NOT_ABILITY_UPDATE);
        }
        //修改页面
        if (page.getId() != null) {
            UpdateWrapper<Page> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", page.getId());
            updateWrapper.set("page_name", page.getPageName());
            updateWrapper.set("page_label", page.getPageLabel());
            updateWrapper.set("page_cover", page.getPageCover());
            updateWrapper.set("update_time", new Date());
            boolean update = this.update(updateWrapper);
            // 删除缓存
            redisService.del(RedisPrefixConst.PAGE_COVER);
            if (!update) {
                return Result.getPageResult(null, PageEnum.UPDATE_PAGE_ERROR);
            }
            return Result.getPageResult(null, PageEnum.UPDATE_PAGE_SUCCESS);
        }
        page.setCreateTime(new Date());
        boolean save = this.save(page);
        if (!save) {
            return Result.getPageResult(null, PageEnum.SAVE_PAGE_ERROR);
        }
        // 删除缓存
        redisService.del(RedisPrefixConst.PAGE_COVER);
        return Result.getPageResult(null, PageEnum.SAVE_PAGE_SUCCESS);
    }

    @Override
    public Result<?> deletePage(Integer id, HttpServletRequest request) {
        String token = request.getHeader("token");
        Claims claims = JWTUtils.parseToken(token);
        Object userId = claims.get("id");
        User user = userService.getById((Serializable) userId);
        if (user.getRole() != 2) {
            return Result.getPageResult(null, PageEnum.NOT_ABILITY_UPDATE);
        }
        boolean remove = this.removeById(id);
        if (!remove) {
            return Result.getPageResult(null, PageEnum.DELETE_PAGE_ERROR);
        }
        // 删除缓存
        redisService.del(RedisPrefixConst.PAGE_COVER);
        return Result.getPageResult(null, PageEnum.DELETE_PAGE_SUCCESS);
    }

    @Override
    public List<Page> listPages() {
        List<Page> pageList;
        // 查找缓存信息，不存在则从mysql读取，更新缓存
        Object pageLists = redisService.get(RedisPrefixConst.PAGE_COVER);
        if (Objects.nonNull(pageLists)) {
            pageList = JSON.parseObject(pageLists.toString(), List.class);
        } else {
            pageList = BeanCopyUtils.copyList(this.list(), Page.class);
            redisService.set(RedisPrefixConst.PAGE_COVER, JSON.toJSONString(pageList));
        }
        return pageList;
    }

}
