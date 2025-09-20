package com.cloudblog.common.pojo.Dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userAccount;

    private String phone;

    private String email;

    private String password;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private Integer permissionId;

    private LocalDateTime createTime;
}
