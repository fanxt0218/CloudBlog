package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class WebSiteComponentPo {

    private String category;

    private String contentType;

    WebSiteComponentPo() {}

    public WebSiteComponentPo(String category, String contentType) {
        this.category = category;
        this.contentType = contentType;
    }
}
