package com.cloudblog.content.service;

public interface CommentService {

    Long calculateCommentCount(Long contentId, Integer type);

    /**
     * 获取用户评论数
     * @param userId
     * @return
     */
    Integer getUserCommentCount(Long userId);
}
