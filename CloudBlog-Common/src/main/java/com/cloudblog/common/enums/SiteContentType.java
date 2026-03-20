package com.cloudblog.common.enums;

import com.cloudblog.common.pojo.DoMain.SiteContent;

public enum SiteContentType {

    CAROUSEL("首页轮播图", "carousel"),

    ADVERTISE("首页广告轮播", "advertise"),

    RECOMMEND_TOOL("首页推荐工具", "recommend_tool");

    private String name;

    private String value;

    SiteContentType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
