package com.cloudblog.content.service;

public interface BrowseService {

    /**
     * 获取动态浏览量
     * @param contentId
     * @return
     */
    Long calculateBrowseCount(Long contentId, Integer type);
}
