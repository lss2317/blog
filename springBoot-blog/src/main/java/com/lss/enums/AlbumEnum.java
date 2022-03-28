package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月13日 15:10
 */
@Getter
@AllArgsConstructor
public enum AlbumEnum {
    LIST_ALBUM_SUCCESS(200, "查询相册列表成功"),
    SAVE_ALBUM_SUCCESS(200, "添加相册成功"),
    SAVE_ALBUM_ERROR(500, "添加相册失败"),
    UPDATE_ALBUM_SUCCESS(200, "修改相册成功"),
    UPDATE_ALBUM_ERROR(500, "修改相册失败"),
    DELETE_ALBUM_SUCCESS(200, "删除相册成功"),
    DELETE_ALBUM_ERROR(200, "删除相册失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
