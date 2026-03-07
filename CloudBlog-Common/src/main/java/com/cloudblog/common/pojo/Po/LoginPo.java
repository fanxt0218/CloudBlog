package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class LoginPo {

    private String loginType;

    private String target;

    private String password;

    private String twicePassword;

    private boolean isAdmin;
}
