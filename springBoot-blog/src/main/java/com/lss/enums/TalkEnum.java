package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月10日 21:00
 */
@Getter
@AllArgsConstructor
public enum TalkEnum {
    SAVE_TALK_SUCCESS(200, "添加说说成功"),
    SAVE_TALK_ERROR(500, "添加说说失败"),
    UPDATE_TALK_SUCCESS(200, "修改说说成功"),
    UPDATE_TALK_ERROR(500, "修改说说失败"),
    SELECT_TALK_SUCCESS(200, "查询说说成功"),
    DELETE_TALK_SUCCESS(200, "删除说说成功"),
    DELETE_TALK_ERROR(500, "删除说说失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");


    private final Integer code;
    private final String message;
}
