package com.cloudblog.common.pojo.Dto;

import lombok.Data;

@Data
public class WorkOrderDetailInfo {

    private String orderId;

    private Long targetId;

    private String mainInfo;

    private String detail;

    private String status;

    private String createTime;
}
