package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractionTrendVo {

    private String type;

    private List<ContentTrend> contentTrends;

    @Data
    public static class ContentTrend {

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;

        private Integer count;
    }
}
