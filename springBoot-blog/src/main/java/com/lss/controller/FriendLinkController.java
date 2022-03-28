package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.FriendLink;
import com.lss.service.FriendLinkService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 友链控制层
 *
 * @author lss
 * @create 2022年03月16日 20:39
 */
@RestController
@RequestMapping("link")
public class FriendLinkController {

    @Resource
    FriendLinkService friendLinkService;
    @Resource
    HttpServletRequest request;

    /**
     * 添加或修改友链
     */
    @PostMapping("saveOrUpdateLink")
    @OptLog(ApiModule = "友链模块", ApiDesc = "添加或修改友链", optType = OptTypeConst.SAVE_OR_UPDATE)
    public Result<?> saveOrUpdateLink(@RequestBody FriendLink friendLink) {
        return friendLinkService.addOrUpdateLink(friendLink, request);
    }

    /**
     * 分页查询友链列表
     */
    @GetMapping("listFriendLink")
    public Result<JSONObject> listFriendList(@RequestParam Integer currentPage, @RequestParam Integer pageSize, String keywords) {
        if (keywords == null) {
            keywords = "";
        }
        return friendLinkService.listFriendLink(currentPage, pageSize, keywords);
    }

    /**
     * 删除友链
     *
     * @param friendLink 接收数据实体类
     */
    @PostMapping("deleteLink")
    @OptLog(ApiModule = "友链模块", ApiDesc = "删除友链", optType = OptTypeConst.REMOVE)
    public Result<?> deleteLink(@RequestBody FriendLink friendLink) {
        return friendLinkService.deleteLink(friendLink.getId(), request);
    }

    /**
     * 批量删除友链
     */
    @DeleteMapping("deleteLinks")
    @OptLog(ApiModule = "友链模块", ApiDesc = "批量删除友链", optType = OptTypeConst.REMOVE)
    public Result<?> deleteLinks(@RequestBody List<Integer> linkIdList) {
        return friendLinkService.deleteLinks(linkIdList, request);
    }

    /**
     * 前台获取友链集合
     *
     * @return 友链List集合
     */
    @GetMapping("listLink")
    public List<FriendLink> listLink() {
        return friendLinkService.listLink();
    }
}
