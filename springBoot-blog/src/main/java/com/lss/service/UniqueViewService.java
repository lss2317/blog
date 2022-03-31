package com.lss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.entity.UniqueView;

import java.util.List;

/**
 * 网站访问量服务类
 *
 * @author lss
 * @create 2022年03月30日 22:24
 */
public interface UniqueViewService extends IService<UniqueView> {

    /**
     * 获取7天用户量统计
     *
     * @return 用户量
     */
    List<UniqueView> listUniqueViews();

}
