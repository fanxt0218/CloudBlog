package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class ChatPo {

    private Long userId;

    private Long targetId;

    private String content;

    private Integer contentType;
}
