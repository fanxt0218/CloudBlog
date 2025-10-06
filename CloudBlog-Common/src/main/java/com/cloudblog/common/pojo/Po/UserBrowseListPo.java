package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserBrowseListPo {

    private Long userId;

    private LocalDate beginTime;

    private LocalDate endTime;

    private Integer pageNum;

    private Integer pageSize;
}
