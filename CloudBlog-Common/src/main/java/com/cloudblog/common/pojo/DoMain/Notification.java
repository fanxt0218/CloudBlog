package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recipientId;

    private Long senderId;

    /**
     * 0:点赞 1:收藏 2:评论 3:关注
     */
    private Integer type;

    /**
     * 0:文章 1:动态 2:其他
     */
    private Integer objectType;

    private Long objectId;

    private String content;

    private Integer isRead;

    private Integer isAggregated;

    private Integer aggregatedCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String userName;

    private String userImage;
}
