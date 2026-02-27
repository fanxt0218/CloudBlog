package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class ReviewOpinionPo {

    /**
     * 1通过 2拒绝
     */
    private Integer opinion;

    private String reason;
}
