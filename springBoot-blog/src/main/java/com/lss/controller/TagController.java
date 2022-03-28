package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Tag;
import com.lss.enums.TagEnum;
import com.lss.service.TagService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 标签控制器
 *
 * @author lss
 * @create 2022年02月11日 15:59
 */
@RestController
@RequestMapping("tag")
public class TagController {

    @Resource
    TagService tagService;
    @Resource
    HttpServletRequest request;

    /**
     * 修改标签
     */
    @PostMapping("modifyTag")
    @OptLog(ApiModule = "标签模块", ApiDesc = "修改标签", optType = OptTypeConst.UPDATE)
    public Result<?> modifyTag(@RequestBody Tag tag) {
        return tagService.modifyTag(tag, request);
    }

    /**
     * 增加标签
     */
    @PostMapping("addTag")
    @OptLog(ApiModule = "标签模块", ApiDesc = "新增标签", optType = OptTypeConst.SAVE)
    public Result<?> addTag(@RequestBody Tag tag) {
        return tagService.addTag(tag, request);
    }

    /**
     * 通过搜索内容或者分页消息获取标签列表
     *
     * @param pageSize    页码
     * @param currentPage 每页条数
     * @param keywords    搜索关键词
     */
    @GetMapping("listTag")
    public Result<JSONObject> listTag(@RequestParam("pageSize") Integer pageSize, @RequestParam("currentPage") Integer currentPage, @Param("keywords") String keywords) {
        JSONObject json = new JSONObject();
        if (keywords == null || keywords.trim().equals("")) {
            long count = tagService.count();
            json.put("count", count);
            currentPage = (currentPage - 1) * pageSize;
            List<Tag> tags = tagService.listTag(pageSize, currentPage);
            json.put("data", tags);
            return Result.getTagResult(json, TagEnum.SEARCH_TAGS_SUCCESS);
        }
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("tag_name", keywords.trim());
        long count = tagService.count(queryWrapper);
        json.put("count", count);
        currentPage = (currentPage - 1) * pageSize;
        List<Tag> tags = tagService.listTagByTagName(pageSize, currentPage, keywords.trim());
        json.put("data", tags);
        return Result.getTagResult(json, TagEnum.SEARCH_TAGS_SUCCESS);
    }

    /**
     * 删除标签
     */
    @GetMapping("deleteTag")
    @OptLog(ApiModule = "标签模块", ApiDesc = "删除标签", optType = OptTypeConst.REMOVE)
    public Result<?> deleteTag(@Param("id") Integer id) {
        return tagService.deleteTag(id, request);
    }

    /**
     * 批量删除标签
     */
    @DeleteMapping("deleteTags")
    @OptLog(ApiModule = "标签模块", ApiDesc = "批量删除标签", optType = OptTypeConst.REMOVE)
    public Result<?> deleteTags(@RequestBody List<Integer> tagIdList) {
        return tagService.deleteTags(tagIdList, request);
    }

    /**
     * 文章页标签列表
     */
    @GetMapping("search")
    public Result<List<Tag>> searchTags(@Param("keywords") String keywords) {
        if (keywords == null || keywords.equals("")) {
            List<Tag> list = tagService.list();
            return Result.getTagResult(list, TagEnum.SEARCH_TAGS_SUCCESS);
        }
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("tag_name", keywords);
        List<Tag> list = tagService.list(queryWrapper);
        return Result.getTagResult(list, TagEnum.SEARCH_TAGS_SUCCESS);
    }

    /**
     * 获取所有标签
     */
    @GetMapping
    public Result<List<Tag>> listTags() {
        List<Tag> list = tagService.list();
        return Result.getTagResult(list, TagEnum.SEARCH_TAGS_SUCCESS);
    }
}
