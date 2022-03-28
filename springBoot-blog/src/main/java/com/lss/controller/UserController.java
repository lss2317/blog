package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.User;
import com.lss.enums.UserEnum;
import com.lss.service.UserService;
import com.lss.strategy.QQLoginStrategy;
import com.lss.strategy.WeiBoLoginStrategy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户控制器
 *
 * @author lss
 * @create 2022年02月27日 20:00
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    UserService userService;
    @Resource
    QQLoginStrategy qqLoginStrategy;
    @Resource
    HttpServletRequest request;
    @Resource
    WeiBoLoginStrategy weiBoLoginStrategy;

    /**
     * 后台登录登录验证
     */
    @PostMapping("admin/login")
    public Result<?> login(@RequestBody User user) {
        return userService.login(user);
    }

    /**
     * 获取用户列表
     *
     * @param currentPage 页码
     * @param pageSize    每页个数
     * @param loginType   登录类型
     * @param keywords    用户名关键词
     */
    @GetMapping("listUser")
    public Result<JSONObject> listUser(@RequestParam Integer currentPage,
                                       @RequestParam Integer pageSize,
                                       @RequestParam(defaultValue = "0") Integer loginType,
                                       @RequestParam(defaultValue = "") String keywords) {
        List<User> list = userService.listUsers(currentPage, pageSize, loginType, keywords.trim());
        JSONObject json = new JSONObject();
        json.put("data", list);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (loginType != 0) {
            queryWrapper.eq("login_type", loginType);
        }
        if (!keywords.trim().equals("")) {
            queryWrapper.like("nickname", keywords.trim());
        }
        long count = userService.count(queryWrapper);
        json.put("count", count);
        return Result.getUserResult(json, UserEnum.LOGIN_SUCCESS);
    }

    /**
     * 使用QQ登录
     */
    @PostMapping("qq/login")
    public Result<?> qqLogin(@RequestBody JSONObject jsonObject) {
        return qqLoginStrategy.qqLogin(jsonObject);
    }

    /**
     * 使用微博登录
     */
    @PostMapping("weibo/login")
    public Result<?> weiboLogin(@RequestBody JSONObject jsonObject){
        return weiBoLoginStrategy.weiboLogin(jsonObject);
    }

    /**
     * 用户修改角色和昵称
     */
    @PostMapping("updateRole")
    @OptLog(ApiModule = "用户模块", ApiDesc = "修改角色信息", optType = OptTypeConst.UPDATE)
    public Result<?> updateRole(@RequestBody User user, HttpServletRequest request) {
        return userService.updateRole(user, request);
    }

    /**
     * 修改是否禁用
     *
     * @param id        用户id
     * @param isDisable 是否禁用
     */
    @GetMapping("changeDisable")
    @OptLog(ApiModule = "用户模块", ApiDesc = "禁用用户操作", optType = OptTypeConst.UPDATE)
    public Result<?> changeDisable(@RequestParam Integer id, @RequestParam Integer isDisable) {
        return userService.changeDisable(id, isDisable, request);
    }

    /**
     * 获取个人中心信息
     */
    @GetMapping("myself")
    public Result<?> getSelf() {
        return userService.getSelf();
    }

    /**
     * 修改个人中心信息
     *
     * @param user 用户实体类接收
     * @return 反馈信息
     */
    @PostMapping("updateInfo")
    @OptLog(ApiModule = "用户模块", ApiDesc = "修改个人信息", optType = OptTypeConst.UPDATE)
    public Result<?> updateInfo(@RequestBody User user) {
        return userService.updateInfo(user);
    }

    /**
     * 修改密码
     *
     * @param json json接收数据
     */
    @PostMapping("updatePassword")
    @OptLog(ApiModule = "用户模块", ApiDesc = "修改密码", optType = OptTypeConst.UPDATE)
    public Result<?> updatePassword(@RequestBody JSONObject json) {
        return userService.updatePassword(json.getString("oldPassword"), json.getString("newPassword"));
    }

    /**
     * 博客前台登录
     *
     * @param user 接收数据
     */
    @PostMapping("login")
    public Result<?> blogLogin(@RequestBody User user) {
        return userService.blogLogin(user);
    }

    /**
     * 修改头像
     *
     * @param user 接收数据
     */
    @PutMapping("changeAvatar")
    @OptLog(ApiModule = "用户模块", ApiDesc = "更换头像", optType = OptTypeConst.UPDATE)
    public Result<?> changeAvatar(@RequestBody User user) {
        return userService.changeAvatar(user);
    }
}
