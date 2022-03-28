package com.lss.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lss
 * @create 2022年02月20日 20:18
 */
@Getter
@AllArgsConstructor
public enum ArticleEnum {
    CHANGE_TOP_SUCCESS(200, "修改成功"),
    CHANGE_TOP_ERROR(500, "修改失败"),
    SAVE_ARTICLE_SUCCESS(200, "添加文章成功"),
    SAVE_ARTICLE_ERROR(500, "添加文章失败"),
    SEARCH_ARTICLE_SUCCESS(200, "获取文章列表成功"),
    MODIFY_ARTICLE_SUCCESS(200, "修改文章成功"),
    MODIFY_ARTICLE_ERROR(500, "修改文章失败"),
    RESTORE_ARTICLE_SUCCESS(200, "恢复文章成功"),
    RESTORE_ARTICLE_ERROR(500, "恢复文章失败"),
    DELETE_ARTICLE_SUCCESS(200, "删除文章成功"),
    DELETE_ARTICLE_ERROR(500, "删除文章失败"),
    NOT_ABILITY_UPDATE(400, "权限不足");
    private final Integer code;
    private final String message;
}
