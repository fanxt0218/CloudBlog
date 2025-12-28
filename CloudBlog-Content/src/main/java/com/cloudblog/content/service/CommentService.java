package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Vo.CommentListVo;

import java.util.List;

public interface CommentService {

    Long calculateCommentCount(Long contentId, Integer type);

    /**
     * 获取用户评论数
     * @param userId
     * @return
     */
    Integer getUserCommentCount(Long userId);

    List<CommentListVo> getComments(Long contentId, Integer type, Long parentId, Long userId);
}
