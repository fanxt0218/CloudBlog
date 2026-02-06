package com.cloudblog.common.pojo.DoMain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSearchHistory {

    private Long id;

    private Long userId;

    private String keyword;

    private String searchType;

    private String ipAddress;

    private String device;

    private Integer resultCount;

    private LocalDateTime createTime;

    private Integer isDeleted;
}
