package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListVo {

    private Long commentId;

    private Long userId;

    private String userName;

    private String userAvatar;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Long childCount;

    private Long likeCount;

    private Boolean isLike;

    private Integer level;

    private String parentUserName;

    private Long parentCommentId;

    private List<CommentListVo> children;

}
