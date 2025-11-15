package com.cloudblog.common.pojo.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFanNoticeList {

    private Long userId;

    private String userName;

    private String userImage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String content;

    private Integer isRead;
}
