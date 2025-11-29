package com.cloudblog.common.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class UserAchievementVo {

    private Integer likeCount;

    private Integer commentCount;

    private Integer collectCount;

    private Long rank;

    List<CreativeProcess> creativeProcessList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreativeProcess {

        private Integer year;

        private Integer postCount;


    }
}
