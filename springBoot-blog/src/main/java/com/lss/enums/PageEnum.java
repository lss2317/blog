package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 页面枚举
 *
 * @author lss
 * @create 2022年03月17日 11:09
 */
@Getter
@AllArgsConstructor
public enum PageEnum {

    SEARCH_PAGE_SUCCESS(200, "查询成功"),
    SAVE_PAGE_SUCCESS(200, "添加页面成功"),
    SAVE_PAGE_ERROR(500, "添加页面失败"),
    UPDATE_PAGE_SUCCESS(200, "修改页面成功"),
    UPDATE_PAGE_ERROR(500, "修改页面失败"),
    DELETE_PAGE_SUCCESS(200, "删除页面成功"),
    DELETE_PAGE_ERROR(500, "删除页面失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
