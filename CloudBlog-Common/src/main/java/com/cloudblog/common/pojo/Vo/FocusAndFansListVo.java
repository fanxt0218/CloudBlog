package com.cloudblog.common.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FocusAndFansListVo {

    private Long userId;

    private String userName;

    private String avatar;

    private String introduction;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 列表类型 0关注列表 1粉丝列表
     */
    private Integer ListType;

    /**
     * 关注状态 0未关注 1已关注
     */
    private Integer followStatus;
}
