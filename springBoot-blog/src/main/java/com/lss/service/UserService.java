package com.lss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务类
 *
 * @author lss
 * @create 2022年02月27日 19:59
 */
public interface UserService extends IService<User> {

    /**
     * 发送邮箱验证码
     *
     * @param username 邮箱号
     */
    void sendCode(String username);

    /**
     * 分页查询用户
     *
     * @param currentPage 页码
     * @param pageSize    每页个数
     * @param loginType   登录类型
     * @param keywords    用户名关键词
     */
    List<User> listUsers(Integer currentPage, Integer pageSize, Integer loginType, String keywords);

    /**
     * 登录验证
     *
     * @param user 用户实体类
     */
    Result<?> login(User user);

    /**
     * 用户修改角色和昵称
     *
     * @param user    用户实体类
     * @param request 请求，获取token
     */
    Result<?> updateRole(@RequestBody User user, HttpServletRequest request);

    /**
     * 修改是否禁用
     *
     * @param id        用户id
     * @param isDisable 是否禁用
     */
    Result<?> changeDisable(@Param("id") Integer id, @Param("isDisable") Integer isDisable, HttpServletRequest request);

    /**
     * 获取个人中心信息
     */
    Result<?> getSelf();

    /**
     * 修改个人中心信息
     *
     * @param user 用户实体类接收
     * @return 反馈信息
     */
    Result<?> updateInfo(User user);

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 反馈前端信息
     */
    Result<?> updatePassword(String oldPassword, String newPassword);

    /**
     * 前台登录验证
     *
     * @param user 接收账号密码
     * @return 反馈信息
     */
    Result<?> blogLogin(User user);

    /**
     * 更换头像
     */
    Result<?> changeAvatar(User user);
}
