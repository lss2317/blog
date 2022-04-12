package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.ChatRecord;
import com.lss.entity.WebsiteConfig;
import com.lss.service.BlogInfoService;
import com.lss.service.impl.WebSocketServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 博客信息控制层
 *
 * @author lss
 * @create 2022年03月18日 15:11
 */
@RestController
@RequestMapping("blogInfo")
public class BlogInfoController {

    @Resource
    BlogInfoService blogInfoService;
    @Resource
    WebSocketServiceImpl webSocketService;


    /**
     * 获取关于我信息
     */
    @GetMapping("getAbout")
    public String getAbout() {
        return blogInfoService.getAbout();
    }

    /**
     * 修改关于我信息
     */
    @PostMapping("updateAbout")
    @OptLog(ApiModule = "配置模块", ApiDesc = "修改关于我", optType = OptTypeConst.UPDATE)
    public Result<?> updateAbout(@RequestBody String aboutContent) {
        return blogInfoService.updateAbout(aboutContent);
    }

    /**
     * 保存或更新网站配置
     *
     * @return 结果信息
     */
    @PostMapping("updateWebsiteConfig")
    @OptLog(ApiModule = "配置模块", ApiDesc = "保存或更新配置", optType = OptTypeConst.SAVE_OR_UPDATE)
    public Result<?> updateWebsiteConfig(@RequestBody WebsiteConfig websiteConfig) {
        return blogInfoService.updateWebsiteConfig(websiteConfig);
    }

    /**
     * 获取网站配置信息
     *
     * @return 信息结果
     */
    @GetMapping("getWebsiteConfig")
    public WebsiteConfig getWebsiteConfig() {
        return blogInfoService.getWebsiteConfig();
    }

    /**
     * 获取前台主页信息
     *
     * @return json数据信息
     */
    @GetMapping("getBlogInfo")
    public JSONObject getBlogInfo() {
        return blogInfoService.getBlogInfo();
    }

    /**
     * 添加访问量
     */
    @GetMapping("report")
    public void report() {
        blogInfoService.report();
    }

    /**
     * 获取后台主页信息
     *
     * @return json数据
     */
    @GetMapping("adminBlogInfo")
    public JSONObject getBlogBackInfo() {
        return blogInfoService.getBlogBackInfo();
    }

    /**
     * 保存语音信息
     *
     * @param chatRecord 语音信息
     */
    @PostMapping("/voice")
    public void sendVoice(ChatRecord chatRecord) {
        webSocketService.sendVoice(chatRecord);
    }
}
