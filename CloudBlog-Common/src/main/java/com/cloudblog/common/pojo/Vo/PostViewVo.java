package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostViewVo {

    private Long postId;

    private Long authorId;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer storeType;

    private String content;

    private Long browseCount;

    private Long likeCount;

    private Long collectCount;

    private Long commentCount;

    private List<PostTagInfo> tagList;

    private Integer status;

    /**
     * 可见范围
     */
    private Integer type;

    /**
     * 帖子类型
     */
    private Integer postType;

    /**
     * 是否是会员可见
     */
    private Integer isVip;

    private Integer categoryId;

    private String categoryName;

    private String categoryCover;

    private Integer liked;

    private Integer collected;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostTagInfo {

        private Integer tagId;

        private String tagName;
    }
}
