package com.cloudblog.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.exception.Error;
import com.cloudblog.common.pojo.DoMain.PostForbiddenWords;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ErrorMapper extends BaseMapper<Error> {

    /**
     * 插入全局异常
     * @param simpleName
     * @param message
     * @param stackTrace
     */
    void insertGlobalException(@Param("simpleName") String simpleName, @Param("message") String message, @Param("stackTrace") String stackTrace);

    /**
     * 获取敏感词列表
     * @return
     */
    List<PostForbiddenWords> getSensitiveWords();
}
