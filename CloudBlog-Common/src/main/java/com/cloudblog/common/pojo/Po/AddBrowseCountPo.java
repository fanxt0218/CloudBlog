package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class AddBrowseCountPo {

    private Long userId;

    private Long postId;

    /**
     * 0 文章 1 动态
     */
    private Integer contentType;
}
