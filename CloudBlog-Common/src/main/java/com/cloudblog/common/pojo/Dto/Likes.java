package com.cloudblog.common.pojo.Dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("likes")
public class Likes {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long targetId;

    private Long userId;

    /**
     * 0:文章 1:评论 2:动态
     */
    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 0:正常 1：取消
     */
    private Integer status;
}
