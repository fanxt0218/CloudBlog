package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserListPo {

    private String name;

    private String account;

    private String email;

    private String phone;

    private Integer status;

    private LocalDate startTime;

    private LocalDate endTime;

    private Integer pageNum;

    private Integer pageSize;
}
