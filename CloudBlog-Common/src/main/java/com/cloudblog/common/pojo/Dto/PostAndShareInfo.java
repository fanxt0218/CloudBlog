package com.cloudblog.common.pojo.Dto;

import lombok.Data;

@Data
public class PostAndShareInfo {

    private Long id;

    private Long author_id;

    private String intro;

    private Integer status;
}
