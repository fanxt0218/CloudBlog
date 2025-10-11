package com.cloudblog.common.exception;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName(value = "error_log")
public class Error {

    @TableId(type = IdType.AUTO)
    private Long id;

    private CommonError errType;

    private String message;

    private String stackTrace;

    private LocalDateTime createTime;

    public Error(CommonError errType, String message) {
        this.errType = errType;
        this.message = message;
        this.createTime = LocalDateTime.now();
    }

    public Error(CommonError errType, String message, String stackTrace) {
        this.errType = errType;
        this.message = message;
        this.stackTrace = stackTrace;
        this.createTime = LocalDateTime.now();
    }
}
