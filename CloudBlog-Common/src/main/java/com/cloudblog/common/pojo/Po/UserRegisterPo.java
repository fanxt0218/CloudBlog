package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class UserRegisterPo {

    private String userName;

    private String phone;

    private String password;

    private String twicePassword;
}
