package com.cloudblog.content.service;

import com.cloudblog.common.result.AjaxResult;

public interface InterestService {

    /**
     * 获取用户兴趣信息
     * @param userId
     * @return
     */
    AjaxResult getInterestInfo(Long userId);
}
