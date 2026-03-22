package com.cloudblog.common.enums;

import com.cloudblog.common.pojo.DoMain.SiteContent;

public enum SiteContentType {

    CAROUSEL("首页轮播图", "carousel"),

    ADVERTISE("首页广告轮播", "advertise"),

    RECOMMEND_TOOL("首页推荐工具", "recommend_tool"),

    LOGIN_BG("登录页背景", "login_bg"),

    LOGIN_MANAGE_BG("管理端登录页背景", "login_manage_bg"),

    VIEWPAGE_BG("浏览页背景", "viewpage_bg");

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
