package com.cloudblog.common.pojo.Dto;

import com.cloudblog.common.pojo.DoMain.Posts;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ESPost {

    private Long id;

    private Long authorId;

    private Integer exp;

    private Integer authorLevel;

    private String authorName;

    private String title;

    private String introduction;

    private String content;

    private Integer categoryId;

    private Integer status;

    private Integer type;

    private Integer postType;

    private Integer isVip;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;

    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    private Long collectCount;
}
