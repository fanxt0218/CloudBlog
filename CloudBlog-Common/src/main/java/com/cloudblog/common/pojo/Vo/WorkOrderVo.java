package com.cloudblog.common.pojo.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkOrderVo {

    private Long id;

    private String orderId;

    private Long userId;

    private String userName;

    private Long targetId;

    /**
     * 目标类型 0:文章 1:动态 2:评论 3:账号 4:建议
     */
    private Integer targetType;

    /**
     * 工单类型，枚举
     */
    private Integer orderType;

    private String reason;

    private String filePath;

    /**
     * 0:待处理 1:处理中 2:处理完成
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private String handleReason;

}
