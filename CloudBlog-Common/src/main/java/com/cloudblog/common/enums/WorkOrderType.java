package com.cloudblog.common.enums;

public enum WorkOrderType {

    CONTENT_REPORT(0, "内容举报"),
    USER_REPORT(1, "BUG"),
    SUGGEST(2, "建议"),
    FORGET_PASSWORD(3, "忘记密码");

    private Integer code;
    private String message;

    WorkOrderType(Integer code, String message) {
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
