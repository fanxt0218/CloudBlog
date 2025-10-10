package com.cloudblog.common.pojo.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLikeAndCollectNoticeList {

    private Long userId;

    private String userName;

    private String userImage;

    private Integer objectType;

    private Long objectId;

    private String objectTitle;

    private LocalDateTime createTime;

    private Integer isRead;

    private String relationship;
}
