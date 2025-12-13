package com.cloudblog.content.service;

import com.cloudblog.common.result.AjaxResult;

public interface ShareService {

    AjaxResult getUserShareList(Long userId, String cursor, Integer size, String sortBy, String tag);

    /**
     * 添加动态浏览数
     * @param postId
     * @param userId
     */
    void addShareBrowseCount(Long postId, Long userId);

    AjaxResult getIndexShareList(String cursor, Integer size, Integer topicId);

    AjaxResult getTopicList();
}
