package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class UpdateEmailPo {

    private Long userId;

    private String newEmail;
}
