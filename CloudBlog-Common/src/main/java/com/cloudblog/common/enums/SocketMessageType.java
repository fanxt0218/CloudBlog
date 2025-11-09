package com.cloudblog.common.enums;

public enum SocketMessageType {

    CHAT("聊天"),

    COMMENT("新评论"),

    NEW_FAN("新粉丝"),

    NEW_LIKE("新点赞"),

    NEW_COLLECT("新收藏");

    private String type;

    SocketMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
