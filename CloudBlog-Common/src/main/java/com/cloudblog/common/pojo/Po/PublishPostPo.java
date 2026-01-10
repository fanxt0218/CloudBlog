package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.util.List;

@Data
public class PublishPostPo {

    private Long userId;

    private String title;

    private String content;

    private String cover;

    private String intro;

    private List<Long> tagIds;

    private Integer categoryId;

    private Integer postType;

    private Integer type;

    private Integer vip;
}
