package com.cloudblog.user.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId
    private Long id;

    private String userAccount;

    private String password;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;
}
