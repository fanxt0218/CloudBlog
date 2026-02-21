package com.cloudblog.common.exception;

public class UnauthorizedException extends RuntimeException {

    int code;

    public UnauthorizedException(String message) {
        super(message);
        this.code = 401;
    }

    public UnauthorizedException(String message, int code) {
        super(message);
        this.code = code;
    }

    public UnauthorizedException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
