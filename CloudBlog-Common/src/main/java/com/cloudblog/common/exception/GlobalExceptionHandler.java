package com.cloudblog.common.exception;

import com.cloudblog.common.mapper.ErrorMapper;
import com.cloudblog.common.result.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ErrorMapper errorMapper;

    /**
     * 自定义异常
     */
    @ResponseBody
    @ExceptionHandler(CloudBlogException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleCloudBlogException(CloudBlogException e) {
        log.error("CloudBlogException: {}", e.getMessage());
//        e.printStackTrace();
        errorMapper.insert(new Error(e.getErrType(), e.getErrMessage(), Arrays.toString(e.getStackTrace())));
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 全局异常
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        e.printStackTrace();
        // 持久化错误日志
        errorMapper.insertGlobalException(e.getClass().getSimpleName(), e.getMessage(), Arrays.toString(e.getStackTrace()));
        return AjaxResult.error("服务器异常");
    }
}
