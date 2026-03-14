package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class AddCategoryPo {

    private Long userId;

    private String categoryName;

    private String categoryDesc;

    private String image;
}
