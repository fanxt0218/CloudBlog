package com.cloudblog.common.exception;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName(value = "err_log")
public class Error {

    @TableId(type = IdType.AUTO)
    private Long id;

    private CommonError errType;

    private String errMsg;

    private String errStack;

    private LocalDateTime createTime;

    public Error(CommonError errType, String message) {
        this.errType = errType;
        this.errMsg = message;
        this.createTime = LocalDateTime.now();
    }

    public Error(CommonError errType, String message, String stackTrace) {
        this.errType = errType;
        this.errMsg = message;
        this.errStack = stackTrace;
        this.createTime = LocalDateTime.now();
    }
}
