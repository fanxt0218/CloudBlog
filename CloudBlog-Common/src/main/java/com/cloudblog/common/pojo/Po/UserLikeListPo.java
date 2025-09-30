package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLikeListPo {

    private Long userId;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Integer pageNum;

    private Integer pageSize;
}
