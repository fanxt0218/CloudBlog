package com.cloudblog.content.service;

import com.cloudblog.common.result.AjaxResult;

public interface LikeService {

    AjaxResult liking(Long userId, Long targetId, Integer status, Integer type);

    /**
     * 获取用户点赞数量
     * @param userId
     * @return
     */
    Integer getUserLikeCount(Long userId);

    /**
     * 计算点赞数量
     * @param contentId
     * @param type
     * @return
     */
    Long calculateLikeCount(Long contentId, Integer type);
}
