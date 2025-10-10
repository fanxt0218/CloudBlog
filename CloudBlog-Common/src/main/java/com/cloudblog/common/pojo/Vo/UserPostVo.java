package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPostVo {

    private Long id;

    private String title;

    private String introduction;

    private String image;

    private Integer type;

    /**
     * 文章类型(0:普通文章/博客 1:新闻/资讯)
     */
    private Integer postType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long browseCount;

    private Long likeCount;

    private Long collectCount;
}
