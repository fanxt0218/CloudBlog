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
}
