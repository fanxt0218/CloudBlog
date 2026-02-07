package com.cloudblog.common.pojo.DoMain;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Integer isDeleted;
}
