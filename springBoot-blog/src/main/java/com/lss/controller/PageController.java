package com.lss.controller;

import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Page;
import com.lss.enums.PageEnum;
import com.lss.service.PageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 页面控制层
 *
 * @author lss
 * @create 2022年03月17日 18:23
 */
@RestController
@RequestMapping("page")
public class PageController {

    @Resource
    PageService pageService;
    @Resource
    HttpServletRequest request;

    /**
     * 添加或修改页面
     */
    @PostMapping("saveOrUpdatePage")
    @OptLog(ApiModule = "页面模块", ApiDesc = "添加或修改页面", optType = OptTypeConst.SAVE_OR_UPDATE)
    public Result<?> saveOrUpdatePage(@RequestBody Page page) {
        return pageService.saveOrUpdatePage(page, request);
    }

    /**
     * 获取页面总列表
     */
    @GetMapping("listPages")
    public Result<List<Page>> listPages() {
        List<Page> list = pageService.listPages();
        return Result.getPageResult(list, PageEnum.SEARCH_PAGE_SUCCESS);
    }

    /**
     * 删除页面
     */
    @PostMapping("deletePage")
    @OptLog(ApiModule = "页面模块", ApiDesc = "删除页面", optType = OptTypeConst.REMOVE)
    public Result<?> deletePage(@RequestBody Page page) {
        return pageService.deletePage(page.getId(), request);
    }
}
