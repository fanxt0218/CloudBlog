package com.cloudblog.common.pojo.Vo;

import lombok.Data;

@Data
public class UserFanListVo {

    private Long userId;

    private String userName;

    private String userImage;

    private String introduction;

    private Integer isFollowEachOther;
}
