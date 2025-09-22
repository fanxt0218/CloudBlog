package com.cloudblog.common.pojo.Vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryInfoListVo {

    private List<CategoryInfo> categoryInfoList;

    @Data
    public static class CategoryInfo {

        private Integer categoryId;

        private String categoryName;

        private String image;

        private Integer postCount;
    }
}
