package com.cloudblog.common.enums;

public enum UserStatus {

    NORMAL(0),

    LOCKED(1),

    DELETED(2);

    private int value;

    UserStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserStatus getByValue(int value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
