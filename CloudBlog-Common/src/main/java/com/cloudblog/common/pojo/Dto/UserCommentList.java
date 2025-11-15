package com.cloudblog.common.pojo.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCommentList {

    private Long id;

    private Long userId;

    private String userName;

    private String userImage;

    private String content;

    private Integer objectType;

    private Long objectId;

    private String objectTitle;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Integer isRead;
}
