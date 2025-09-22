package com.cloudblog.common.pojo.Vo;

import lombok.Data;

@Data
public class UserAchievementVo {

    private Integer likeCount;

    private Integer commentCount;

    private Integer collectCount;

    private Integer rank;
}
