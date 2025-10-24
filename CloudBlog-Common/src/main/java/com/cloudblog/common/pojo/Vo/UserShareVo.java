package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserShareVo {

    private Long id;

    /**
     * 简略信息
     */
    private String brief;

    /**
     * 可能存在的图片
     */
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private Integer contentType;

    private Long browseCount;

    private Long likeCount;

}
