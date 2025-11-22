package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class CategoryDetailPo {

    private Long userId;

    private Integer categoryId;

    private Integer pageSize;

    private Integer pageNum;

    private String postName;
}
