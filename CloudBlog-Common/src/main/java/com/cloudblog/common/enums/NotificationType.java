package com.cloudblog.common.enums;

public enum NotificationType {

    CHAT(1),

    COMMENT(2),

    NEW_FAN(3),

    NEW_LIKE(4),

    NEW_COLLECT(5);

    private int value;

    NotificationType(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

}
