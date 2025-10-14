package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class PostPo {

    private Long userId;

    private Integer tagId;

    /**
     * 0:文章 1：资讯
     */
    private Integer postTye;
}
