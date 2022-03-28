package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.User;

import java.util.List;

/**
 * 用户交互层
 *
 * @author lss
 * @create 2022年02月27日 19:58
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户
     *
     * @param currentPage 页码
     * @param pageSize    每页个数
     * @param loginType   登录类型
     * @param keywords    用户名关键词
     */
    List<User> listUsers(Integer currentPage, Integer pageSize, Integer loginType, String keywords);
}
