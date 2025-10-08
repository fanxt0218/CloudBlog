package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserHomePageVo {

    private String userName;

    private String image;

    private String region;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;

    private String introduction;

    private Long visits;

    private Long postCount;

    private Long fanCount;

    private Integer blogAge;

    private Integer level;

    private Long focusCount;

    private Integer isVip;
}
