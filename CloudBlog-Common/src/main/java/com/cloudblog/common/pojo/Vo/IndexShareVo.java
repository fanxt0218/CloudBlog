package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IndexShareVo {

    private Long id;

    private Long userId;

    private String userName;

    private String userAvatar;

    private Integer topicId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String brief;

    private String image;

    private Long browseCount;

    private Long likeCount;
}
