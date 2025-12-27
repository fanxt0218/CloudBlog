package com.cloudblog.content.service.impl;

import com.cloudblog.content.mapper.CommentMapper;
import com.cloudblog.content.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Long calculateCommentCount(Long contentId, Integer type) {
        return commentMapper.calculateCommentCount(contentId, type);
    }

    @Override
    public Integer getUserCommentCount(Long userId) {
        return commentMapper.getUserCommentCount(userId);
    }
}
