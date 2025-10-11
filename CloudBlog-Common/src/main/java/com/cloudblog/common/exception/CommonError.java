package com.cloudblog.common.exception;

public enum CommonError {

    UNKNOWN_ERROR("未知错误"),
    INTERNAL_ERROR("系统错误");

    private String errMessage;

    CommonError(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
