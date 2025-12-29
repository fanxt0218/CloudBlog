package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class CommentPo {

    private Long userId;

    private Long targetCommentId;

    private Long contentId;

    private String content;

    private Integer type;
}
