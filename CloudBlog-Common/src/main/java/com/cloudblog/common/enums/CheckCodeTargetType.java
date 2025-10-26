package com.cloudblog.common.enums;

public enum CheckCodeTargetType {

    PHONE("phone"),

    EMAIL("email");

    private String value;

    CheckCodeTargetType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
