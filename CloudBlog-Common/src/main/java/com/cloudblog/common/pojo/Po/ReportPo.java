package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class ReportPo {

    private Integer targetType;

    private Long targetId;

    private String reason;

    private String filePath;
}
