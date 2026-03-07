package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InteractionTrendPo {

    private Long userId;

    private LocalDate startDate;

    private LocalDate endDate;
}
