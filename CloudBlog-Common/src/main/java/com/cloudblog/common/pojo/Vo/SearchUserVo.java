package com.cloudblog.common.pojo.Vo;

import lombok.Data;

@Data
public class SearchUserVo {

    private Long userId;

    private String userName;

    private String image;

    private String introduction;

    private Long contentCount;

    private Long viewCount;

    private Long likedCount;

    private Integer follow;
}
