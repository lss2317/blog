package com.lss.common;

import com.lss.enums.*;
import lombok.Data;

/**
 * @author lss
 * @create 2022年02月08日 22:09
 * 数据封装类，用于返回数据给前端
 */
@Data
public class Result<T> {
    /**
     * 状态码
     */
    private int code;
    /**
     * 反馈数据
     */
    private T data;
    /**
     * 返回信息
     */
    private String message;

    public Result() {
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 标签管理
     */
    public static <T> Result<T> getTagResult(T data, TagEnum tagEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(tagEnum.getCode());
        result.setMessage(tagEnum.getMessage());
        return result;
    }

    /**
     * 分类管理
     */
    public static <T> Result<T> getClassificationResult(T data, ClassificationEnum classificationEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(classificationEnum.getCode());
        result.setMessage(classificationEnum.getMessage());
        return result;
    }

    /**
     * 文章管理
     */
    public static <T> Result<T> getArticleResult(T data, ArticleEnum articleEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(articleEnum.getCode());
        result.setMessage(articleEnum.getMessage());
        return result;
    }

    /**
     * 用户管理
     */
    public static <T> Result<T> getUserResult(T data, UserEnum userEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(userEnum.getCode());
        result.setMessage(userEnum.getMessage());
        return result;
    }

    /**
     * 说说管理
     */
    public static <T> Result<T> getTalkResult(T data, TalkEnum talkEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(talkEnum.getCode());
        result.setMessage(talkEnum.getMessage());
        return result;
    }

    /**
     * 相册管理
     */
    public static <T> Result<T> getAlbumResult(T data, AlbumEnum albumEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(albumEnum.getCode());
        result.setMessage(albumEnum.getMessage());
        return result;
    }

    /**
     * 照片管理
     */
    public static <T> Result<T> getPhotoResult(T data, PhotoEnum photoEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(photoEnum.getCode());
        result.setMessage(photoEnum.getMessage());
        return result;
    }

    /**
     * 友链管理
     */
    public static <T> Result<T> getFriendLinkResult(T data, FriendLinkEnum friendLinkEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(friendLinkEnum.getCode());
        result.setMessage(friendLinkEnum.getMessage());
        return result;
    }

    /**
     * 页面管理
     */
    public static <T> Result<T> getPageResult(T data, PageEnum pageEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(pageEnum.getCode());
        result.setMessage(pageEnum.getMessage());
        return result;
    }

    /**
     * 日志管理
     */
    public static <T> Result<T> getLogResult(T data, LogEnum logEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(logEnum.getCode());
        result.setMessage(logEnum.getMessage());
        return result;
    }

    /**
     * 留言管理
     */
    public static <T> Result<T> getMessageResult(T data, MessageEnum messageEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(messageEnum.getCode());
        result.setMessage(messageEnum.getMessage());
        return result;
    }

    /**
     * 评论模块
     */
    public static <T> Result<T> getCommentResult(T data, CommentEnum commentEnum) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(commentEnum.getCode());
        result.setMessage(commentEnum.getMessage());
        return result;
    }

}
