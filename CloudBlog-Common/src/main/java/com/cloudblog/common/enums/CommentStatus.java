package com.cloudblog.common.enums;

public enum CommentStatus {

    NORMAL(0, "正常"),

    DELETED(1, "已删除");

    private final Integer code;

    private final String message;

    CommentStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }
}
