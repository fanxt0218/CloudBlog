package com.cloudblog.common.pojo.Vo;

import lombok.Data;

@Data
public class PublishPageTopicListVo extends IndexTopicVo{

    private String cover;

    private String description;

    private Long participateCount;

}
