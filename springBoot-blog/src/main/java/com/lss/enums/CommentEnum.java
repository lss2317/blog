package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年03月26日 15:28
 */
@Getter
@AllArgsConstructor
public enum CommentEnum {

    LIST_COMMENT_SUCCESS(200, "查询评论成功"),
    DELETE_COMMENT_SUCCESS(200, "删除评论成功"),
    DELETE_COMMENT_ERROR(500, "删除评论失败"),
    CHECK_COMMENT_SUCCESS(200, "评论审核通过"),
    CHECK_COMMENT_ERROR(500, "评论通过失败"),
    SAVE_COMMENT_SUCCESS(200, "添加评论成功"),
    SAVE_COMMENT_ERROR(500, "添加评论失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");

    private final Integer code;
    private final String message;
}
