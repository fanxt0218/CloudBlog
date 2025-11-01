package com.cloudblog.common.enums;

/**
 * 会员状态枚举
 */
public enum VipStatus {

    UNOPENED(0, "未开通"),
    OPENED(1, "已开通");

    private Integer code;

    private String message;

    VipStatus(Integer code, String message) {
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
