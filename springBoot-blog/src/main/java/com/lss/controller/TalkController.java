package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Talk;
import com.lss.enums.TalkEnum;
import com.lss.service.TalkService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 说说控制层
 *
 * @author lss
 * @create 2022年03月10日 21:02
 */
@RestController
@RequestMapping("talk")
public class TalkController {

    @Resource
    TalkService talkService;
    @Resource
    HttpServletRequest request;

    /**
     * 添加或修改说说
     *
     * @param talk 说说实体类
     */
    @OptLog(ApiModule = "说说模块", ApiDesc = "修改或发布说说", optType = OptTypeConst.SAVE_OR_UPDATE)
    @PostMapping("saveOrUpdateTalk")
    public Result<?> saveOrUpdateTalk(@RequestBody Talk talk) {
        return talkService.saveOrUpdateTalk(talk, request);
    }

    /**
     * 分页查询说说列表，按照发布时间顺序
     *
     * @param currentPage 页码
     * @param pageSize    每页数据
     * @param status      状态 1、公开 2、私有
     */
    @GetMapping("listTalks")
    public Result<JSONObject> listTalks(@RequestParam Integer currentPage, @RequestParam Integer pageSize, @RequestParam(defaultValue = "0") Integer status) {
        JSONObject json = new JSONObject();
        List<Talk> talks = talkService.listTalks(currentPage, pageSize, status);
        json.put("data", talks);
        if (status != null && status != 0) {
            QueryWrapper<Talk> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", status);
            long count = talkService.count(queryWrapper);
            json.put("count", count);
        } else {
            long count = talkService.count();
            json.put("count", count);
        }
        return Result.getTalkResult(json, TalkEnum.SELECT_TALK_SUCCESS);
    }

    /**
     * 获取单个说说
     *
     * @param talkId 说说id
     */
    @GetMapping("/{talkId}")
    public Result<Talk> selectTalk(@PathVariable Integer talkId) {
        Talk talk = talkService.getOneTalk(talkId);
        return Result.getTalkResult(talk, TalkEnum.SELECT_TALK_SUCCESS);
    }

    /**
     * 删除说说
     *
     * @param talk 接收参数
     */
    @PostMapping("/deleteTalk")
    @OptLog(ApiModule = "说说模块", ApiDesc = "删除说说", optType = OptTypeConst.REMOVE)
    public Result<?> deleteTalk(@RequestBody Talk talk) {
        return talkService.deleteTalk(talk.getId(), request);
    }

    /**
     * 前台查询说说列表
     */
    @GetMapping("blogTalks")
    public Result<?> blogTalks(@RequestParam Integer current, @RequestParam Integer size) {
        return talkService.blogTalks(current, size);
    }

    /**
     * 点赞说说或取消点赞
     *
     * @param talkId 说说id
     */
    @PostMapping("/{talkId}/like")
    public Result<?> saveTalkLike(@PathVariable Integer talkId) {
        try {
            talkService.saveTalkLike(talkId);
        } catch (Exception e) {
            return Result.getTalkResult(null, TalkEnum.UPDATE_TALK_ERROR);
        }
        return Result.getTalkResult(null, TalkEnum.UPDATE_TALK_SUCCESS);
    }
}
