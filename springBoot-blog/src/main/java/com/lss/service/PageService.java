package com.lss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lss
 * @create 2022年03月17日 11:06
 */
public interface PageService extends IService<Page> {

    /**
     * 添加或修改页面
     *
     * @param page    页面实体类
     * @param request 请求
     */
    Result<?> saveOrUpdatePage(Page page, HttpServletRequest request);


    /**
     * 删除页面
     *
     * @param id      删除页面id
     * @param request 请求
     */
    Result<?> deletePage(Integer id, HttpServletRequest request);

    /**
     * 获取页面列表
     *
     * @return {@link List <Page>} 页面列表
     */
    List<Page> listPages();
}
