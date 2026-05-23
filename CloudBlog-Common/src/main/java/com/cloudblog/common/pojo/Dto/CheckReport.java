package com.cloudblog.common.pojo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CheckReport {

    private String checkId;

    private Long postId;

    private String postName;

    private String checkTime;

    private List<CheckItem> checkItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckItem {

        private String itemName;

        private String itemResult;
    }
}
