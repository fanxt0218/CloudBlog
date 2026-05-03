package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class GetIndexResourcePo {

    private Long userId;

    private String name;

    private String tagName;

    private Integer vip;
}
