package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IndexFocusArticleVo {

    private Long articleId;

    private Long userId;

    private String userName;

    private String avatar;

    private String title;

    private String intro;

    private String cover;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 类型（0文章，1动态）
     */
    private Integer type;

    /**
     * 文章类型（0博客，1资讯）
     */
    private Integer postType;
}
