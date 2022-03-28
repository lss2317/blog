package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月16日 20:24
 */
@Getter
@AllArgsConstructor
public enum FriendLinkEnum {
    LIST_LINK_SUCCESS(200, "查询成功"),
    NOT_ABILITY_UPDATE(400, "权限不足"),
    UPDATE_LINK_SUCCESS(200, "修改友链成功"),
    UPDATE_LINK_ERROR(500, "修改友链失败"),
    SAVE_LINK_SUCCESS(200, "添加友链成功"),
    SAVE_LINK_ERROR(500, "添加友链失败"),
    DELETE_LINK_SUCCESS(200, "删除友链成功"),
    DELETE_LINK_ERROR(500, "删除友链失败");

    private final Integer code;
    private final String message;
}
