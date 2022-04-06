package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年02月27日 20:07
 */
@Getter
@AllArgsConstructor
public enum UserEnum {
    LOGIN_SUCCESS(200, "登录成功"),
    REGISTER_USER_SUCCESS(200, "注册成功"),
    LOGIN_ERROR(500, "用户名或密码错误"),
    STRATEGY_LOGIN_ERROR(500, "登录失败"),
    UPDATE_ROLE_SUCCESS(200, "修改成功"),
    UPDATE_ROLE_ERROR(500, "修改失败"),
    NOT_ABILITY_UPDATE(400, "权限不足"),
    LIST_USER_SUCCESS(200, "查询成功"),
    CHANGE_AVATAR_SUCCESS(200, "更换头像成功"),
    CHANGE_AVATAR_ERROR(500, "更换头像失败"),
    PASSWORD_ERROR(404, "旧密码错误"),
    DISABLE_LOGIN(300, "该用户已经被禁用"),
    EMAIL_IS_EXIST(405, "该邮箱已经注册"),
    PASSWORD_NOT_STANDARD(405, "密码不规范"),
    CODE_NOT_CORRECT(405, "验证码错误"),
    LOGIN_TYPE_ERROR(405, "登录类型不是邮箱"),
    EMAIL_NOT_REGISTER(405, "该邮箱还未注册");

    private final Integer code;
    private final String message;
}
