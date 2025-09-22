package com.cloudblog.common.enums;

public enum PostStatus {

    DRAFT(0, "草稿"),
    REVIEWING(1, "待审核"),
    PUBLISHED(2, "已发布"),
    DELETED(2, "已删除");

    private final Integer code;
    private final String message;

    PostStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
