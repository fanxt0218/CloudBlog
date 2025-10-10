package com.cloudblog.common.pojo.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFanNoticeList {

    private Long userId;

    private String userName;

    private String userImage;

    private LocalDateTime createTime;

    private Integer isRead;
}
