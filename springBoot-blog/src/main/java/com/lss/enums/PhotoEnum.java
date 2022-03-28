package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月13日 15:16
 */
@Getter
@AllArgsConstructor
public enum PhotoEnum {
    LIST_PHOTO_SUCCESS(200, "查询照片列表成功"),
    SAVE_PHOTO_SUCCESS(200, "添加照片成功"),
    SAVE_PHOTO_ERROR(500, "添加照片失败"),
    UPDATE_PHOTO_SUCCESS(200, "修改照片成功"),
    UPDATE_PHOTO_ERROR(500, "修改照片失败"),
    DELETE_PHOTO_SUCCESS(200, "删除照片成功"),
    DELETE_PHOTO_ERROR(200, "删除照片失败"),
    MOVE_PHOTO_SUCCESS(200, "照片移动成功"),
    MOVE_PHOTO_ERROR(500, "照片移动失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
