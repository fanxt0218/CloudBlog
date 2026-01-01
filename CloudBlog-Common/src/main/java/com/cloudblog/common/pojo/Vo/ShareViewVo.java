package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShareViewVo {

    private Long id;

    private Long authorId;

    private String userName;

    private String userImage;

    private String content;

    private Integer topicId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String imageUrl;

    private String videoUrl;

    private Long browseCount;

    private Long likeCount;

    private Long commentCount;

    private boolean like;
}
