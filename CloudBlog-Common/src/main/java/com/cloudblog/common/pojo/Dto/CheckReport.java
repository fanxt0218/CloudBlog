package com.cloudblog.common.pojo.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CheckReport {

    private String checkId;

    private Long postId;

    private String postName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    private List<CheckItem> checkItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckItem {

        private String itemName;

        private String itemResult;
    }
}
