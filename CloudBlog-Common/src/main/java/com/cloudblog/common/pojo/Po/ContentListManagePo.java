package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContentListManagePo {

    private String title;

    private String authorName;

    private Integer status;

    private LocalDate startPublishTime;

    private LocalDate endPublishTime;

    private Boolean vip;

    private String sort;

    private Integer pageNum;

    private Integer pageSize;
}
