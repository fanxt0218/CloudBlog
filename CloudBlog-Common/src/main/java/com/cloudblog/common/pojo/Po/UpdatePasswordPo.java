package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class UpdatePasswordPo {

    private Long userId;

    private String newPassword;
}
