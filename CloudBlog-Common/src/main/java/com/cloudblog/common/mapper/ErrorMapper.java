package com.cloudblog.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.exception.Error;
import org.apache.ibatis.annotations.Param;

public interface ErrorMapper extends BaseMapper<Error> {

    /**
     * 插入全局异常
     * @param simpleName
     * @param message
     * @param stackTrace
     */
    void insertGlobalException(@Param("simpleName") String simpleName, @Param("message") String message, @Param("stackTrace") String stackTrace);
}
