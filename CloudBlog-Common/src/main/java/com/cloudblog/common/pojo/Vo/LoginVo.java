package com.cloudblog.common.pojo.Vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVo {

    private Long userId;

    private String token;
}
