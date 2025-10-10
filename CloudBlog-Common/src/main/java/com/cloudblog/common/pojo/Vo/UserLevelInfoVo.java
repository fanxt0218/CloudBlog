package com.cloudblog.common.pojo.Vo;

import lombok.Data;

@Data
public class UserLevelInfoVo {

    private String image;

    private String name;

    private Integer exp;

    private Integer level;

    /**
     * 下一个等级的阈值
     */
    private Integer nextLevelThreshold;
}
