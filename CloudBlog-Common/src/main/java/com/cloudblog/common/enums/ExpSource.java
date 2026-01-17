package com.cloudblog.common.enums;

public enum ExpSource {

    USER_LOGIN(1, "用户登录", 10),

    USER_REGISTER(2, "用户注册", 20),

    USER_UPDATE_INFO(3, "修改资料", 2),

    USER_PUBLISH_POST(4, "发布文章", 5),

    USER_PUBLISH_SHARE(5, "发布动态",3),

    USER_PUBLISH_COMMENT(6, "发布评论", 1),

    USER_BROWSE_POST(7, "浏览文章", 1);

    private final int code;

    private final String message;

    private final Integer exp;

    ExpSource(int code, String message, Integer exp) {
        this.code = code;
        this.message = message;
        this.exp = exp;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getExp() {
        return exp;
    }
}
