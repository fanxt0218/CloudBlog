package com.cloudblog.common.pojo.Vo;

import lombok.Data;

@Data
public class IndexUserListVo {

    private Long userId;

    private String userName;

    private String avatar;

    private Integer exp;

    private Integer level;

    private Integer isVip;

    private Double score;

    private Long postCount;

    private Long fanCount;

    private Long rankNum;
}
