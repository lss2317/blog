package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年02月15日 16:36
 */
@Getter
@AllArgsConstructor
public enum ClassificationEnum {

    ADD_CLASSIFICATION_SUCCESS(200, "添加分类成功"),
    ADD_CLASSIFICATION_ERROR(500, "添加分类失败"),
    DELETE_CLASSIFICATION_SUCCESS(200, "删除分类成功"),
    DELETE_CLASSIFICATION_ERROR(500, "删除分类失败"),
    REPEAT_CLASSIFICATION(100, "分类已经存在,添加失败"),
    MODIFY_CLASSIFICATION_SUCCESS(200, "修改分类成功"),
    MODIFY_CLASSIFICATION_ERROR(500, "修改分类失败"),
    SEARCH_CLASSIFICATION_SUCCESS(200, "查询分类成功"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
