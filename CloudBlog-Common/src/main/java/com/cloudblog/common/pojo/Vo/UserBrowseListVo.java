package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBrowseListVo {

    private Long postId;

    private Long authorId;

    private String title;

    private String image;

    private Integer isVip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
