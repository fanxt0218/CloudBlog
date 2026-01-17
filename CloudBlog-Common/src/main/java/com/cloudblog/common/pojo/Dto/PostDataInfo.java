package com.cloudblog.common.pojo.Dto;

import lombok.Data;

@Data
public class PostDataInfo {

    private Long postId;

    private Long browseCount;

    private Long likeCount;

    private Long collectCount;

    private Long commentCount;
}
