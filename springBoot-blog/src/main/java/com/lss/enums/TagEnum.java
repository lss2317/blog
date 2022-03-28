package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年02月13日 14:15
 */
@Getter
@AllArgsConstructor
public enum TagEnum {

    ADD_TAG_SUCCESS(200, "添加标签成功"),
    ADD_TAG_ERROR(500, "添加标签失败"),
    MODIFY_TAG_SUCCESS(200, "修改标签成功"),
    MODIFY_TAG_ERROR(500, "修改标签失败"),
    REPEAT_TAG(100, "标签已经存在,添加失败"),
    SEARCH_TAGS_SUCCESS(200, "查询成功"),
    DELETE_TAG_SUCCESS(200, "删除标签成功"),
    DELETE_TAG_ERROR(500, "删除标签失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
