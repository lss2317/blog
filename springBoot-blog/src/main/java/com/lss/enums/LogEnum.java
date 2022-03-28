package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月19日 16:01
 */
@Getter
@AllArgsConstructor
public enum LogEnum {

    LIST_LOGS_SUCCESS(200, "查询成功"),
    DELETE_LOGS_SUCCESS(200, "删除日志成功"),
    DELETE_LOGS_ERROR(500, "删除日志失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
