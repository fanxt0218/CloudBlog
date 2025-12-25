package com.cloudblog.common.pojo.Po;

import lombok.Data;

import java.util.List;

@Data
public class PublishSharePo {

    private Long userId;

    private Integer topicId;

    private String content;

    private List<String> imageUrls;

    private List<String> videoUrls;
}
