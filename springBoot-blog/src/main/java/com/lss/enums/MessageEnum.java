package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月22日 15:44
 */
@Getter
@AllArgsConstructor
public enum MessageEnum {
    RESTORE_MESSAGE_SUCCESS(200, "留言审核通过"),
    RESTORE_MESSAGE_ERROR(500, "留言审核通过失败"),
    DELETE_MESSAGE_SUCCESS(200, "删除留言成功"),
    DELETE_MESSAGE_ERROR(500, "删除留言失败"),
    NOT_ABILITY_UPDATE(400, "权限不足"),
    LIST_MESSAGE_SUCCESS(200, "查询留言列表成功");
    private final Integer code;
    private final String message;
}
