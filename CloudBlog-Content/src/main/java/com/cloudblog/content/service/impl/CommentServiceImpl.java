package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.Dto.UserSimpleInfo;
import com.cloudblog.common.pojo.Vo.CommentListVo;
import com.cloudblog.content.mapper.CommentMapper;
import com.cloudblog.content.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<CommentListVo> getComments(Long contentId, Integer type, Long parentId, Long userId) {
        // 判断是否传入父级评论id，没有则加载所有一级评论
        List<CommentListVo> commentList;
        if (parentId == null) {
             commentList = commentMapper.getRootComments(contentId, type);
            // 子评论数量
            for (CommentListVo rootComment : commentList) {
                Long childrenCount = commentMapper.getChildrenCommentCount(rootComment.getCommentId());
                rootComment.setChildCount(childrenCount);
            }
        } else {
            // 获取子级评论
            commentList = commentMapper.getChildrenComments(parentId);
        }
        // 处理点赞
        handleLike(commentList, userId, type);
        return commentList;
    }

    private void handleLike(List<CommentListVo> comments, Long userId, Integer type) {
        for (CommentListVo comment : comments) {
            // 获取点赞数
            comment.setLikeCount(commentMapper.getCommentLikeCount(comment.getCommentId(), type));
            // 判断用户是否点赞
            if (userId != null) {
                List<UserSimpleInfo> userInfo = commentMapper.getCommentLikeUserInfo(comment.getCommentId(), type);
                if (userInfo != null && !userInfo.isEmpty()) {
                    comment.setIsLike(userInfo.stream().anyMatch(user -> user.getUserId().equals(userId)));
                } else {
                    comment.setIsLike(false);
                }
            } else {
                comment.setIsLike(false);
            }
            if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
                handleLike(comment.getChildren(), userId, type);
            }
        }
    }
}
