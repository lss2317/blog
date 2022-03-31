package com.lss.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.WebsiteConfig;

/**
 * 博客信息服务
 *
 * @author lss
 * @create 2022年03月17日 22:00
 */
public interface BlogInfoService extends IService<WebsiteConfig> {

    /**
     * 获取关于我内容
     *
     * @return 关于我内容
     */
    String getAbout();

    /**
     * 修改关于我内容
     *
     * @param aboutContent 博客关于我信息
     */
    Result<?> updateAbout(String aboutContent);

    /**
     * 保存或更新网站配置
     *
     * @param websiteConfig 网站配置
     */
    Result<?> updateWebsiteConfig(WebsiteConfig websiteConfig);

    /**
     * 获取网站配置
     *
     * @return {@link WebsiteConfig} 网站配置
     */
    WebsiteConfig getWebsiteConfig();

    /**
     * 获取前台主页信息
     *
     * @return 返回json数据
     */
    JSONObject getBlogInfo();

    /**
     * 添加访问量
     */
    void report();

    /**
     * 获取后台首页数据
     *
     * @return 博客后台信息
     */
    JSONObject getBlogBackInfo();
}
