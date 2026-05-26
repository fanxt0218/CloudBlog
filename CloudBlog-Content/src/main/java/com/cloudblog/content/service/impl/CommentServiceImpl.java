package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.enums.CommentStatus;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.NotificationType;
import com.cloudblog.common.enums.SocketMessageType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.exception.CommonError;
import com.cloudblog.common.pojo.DoMain.Comments;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.Dto.CommentSourceContent;
import com.cloudblog.common.pojo.Dto.SocketMessage;
import com.cloudblog.common.pojo.Dto.UserSimpleInfo;
import com.cloudblog.common.pojo.Po.CommentPo;
import com.cloudblog.common.pojo.Vo.CommentListVo;
import com.cloudblog.common.pojo.Vo.UserChatDetailVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.ContentComplianceChecker;
import com.cloudblog.content.mapper.CommentMapper;
import com.cloudblog.content.service.CommentService;
import com.cloudblog.content.service.NotificationService;
import com.cloudblog.content.socket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    @Lazy
    private NotificationService notificationService;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private ContentComplianceChecker complianceChecker;

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
        handleLike(commentList, userId, ContentType.COMMENT.ordinal());
        return commentList;
    }

    @Transactional
    @Override
    public AjaxResult comment(CommentPo po) {
        Assert.notNull(po.getContentId(), "内容id不能为空");
        Assert.notNull(po.getUserId(), "用户id不能为空");
        Assert.notNull(po.getType(), "评论类型不能为空");
        // 检测评论内容是否合规
        ContentComplianceChecker.ComplianceResult complianceResult = complianceChecker.checkCompliance(po.getContent());
        if (!complianceResult.isCompliant()) {
            return AjaxResult.warn("内容违规");
        }
        Comments comments = new Comments();
        comments.setPostId(po.getContentId());
        comments.setUserId(po.getUserId());
        comments.setParentId(po.getTargetCommentId());
        comments.setCreateTime(LocalDateTime.now());
        comments.setType(po.getType());
        // 如果目标评论id为空，代表一级评论
        if (po.getTargetCommentId() != null && po.getTargetCommentId() != 0L) {
            commentMapper.comment(comments);
        } else {
            comments.setParentId(0L);
            commentMapper.comment(comments);
        }
        // 添加内容
        commentMapper.addCommentContent(comments.getId(), po.getContent());

        // 发送通知
        Notification notification = new Notification();
        UserSimpleInfo sourceAuthor = commentMapper.getSourceAuthor(po.getContentId(), po.getType());
        CommentSourceContent sourceContent = commentMapper.getSourceContent(po.getContentId(), po.getType());

        boolean ifSendSourceAuthor = true;
        String authorMsg = "";

        // 添加信息表(不为自己时才发送)
        if (po.getUserId().equals(sourceAuthor.getUserId())) {
            if (po.getTargetCommentId() == null || po.getTargetCommentId() == 0L) {
                // 根评论
                String contentPrefix = "评论了你的";
                contentPrefix += switch (po.getType()) {
                    case 0 -> "文章";
                    case 1 -> "动态";
                    default -> "内容";
                };
                contentPrefix += "    [" + sourceContent.getBrief()+"]";
                authorMsg = contentPrefix;

                notification.setContent(contentPrefix+"  "+po.getContent());
            } else {
                // 子评论
                UserSimpleInfo targetUser = commentMapper.getCommentAuthor(po.getTargetCommentId());
                if (!targetUser.getUserId().equals(po.getUserId())) {
                    String contentPrefix = "回复@" + targetUser.getUserName() + "  " + (po.getContent().length() > 20 ? po.getContent().substring(0, 20) : po.getContent());
                    contentPrefix += "    [" + sourceContent.getBrief()+"]";
                    authorMsg = contentPrefix;
                    notification.setContent(contentPrefix);

                    // 发送给目标用户
                    Notification targetUserMsg = new Notification();
                    notification.setContent(po.getContent() + "    ["+ sourceContent.getBrief()+"]");
                    notification.setRecipientId(targetUser.getUserId());
                    notification.setSenderId(po.getUserId());
                    notification.setType(NotificationType.COMMENT.getValue());
                    notification.setObjectType(ContentType.TEXT.ordinal()); // 目前默认为文本，后续可能支持其他类型
                    notification.setIsRead(0);
                    notification.setCreateTime(LocalDateTime.now());
                    notificationService.addNotification(targetUserMsg);

                    // 推送消息
                    UserChatDetailVo.ChatMessage chatMessage = new UserChatDetailVo.ChatMessage();
                    chatMessage.setMessageId(comments.getId());
                    chatMessage.setContent(po.getContent());
                    chatMessage.setSenderId(po.getUserId());
                    chatMessage.setSendTime(notification.getCreateTime());
                    chatMessage.setContentType(ContentType.TEXT.ordinal());

                    webSocket.sendMessage(
                            new SocketMessage<>(SocketMessageType.COMMENT, chatMessage),
                            po.getUserId(),
                            targetUser.getUserId());
                } else {
                    ifSendSourceAuthor = false;
                }
            }
        } else {
            ifSendSourceAuthor = false;
        }

        // 发送给源作者
        if (ifSendSourceAuthor) {
            notification.setRecipientId(sourceAuthor.getUserId());
            notification.setSenderId(po.getUserId());
            notification.setType(NotificationType.COMMENT.getValue());
            notification.setObjectType(ContentType.TEXT.ordinal()); // 目前默认为文本，后续可能支持其他类型
            notification.setIsRead(0);
            notification.setCreateTime(LocalDateTime.now());
            notificationService.addNotification(notification);

            //推送消息
            UserChatDetailVo.ChatMessage chatMessage = new UserChatDetailVo.ChatMessage();
            chatMessage.setMessageId(comments.getId());
            chatMessage.setContent(authorMsg);
            chatMessage.setSenderId(po.getUserId());
            chatMessage.setSendTime(notification.getCreateTime());
            chatMessage.setContentType(ContentType.TEXT.ordinal());
            webSocket.sendMessage(
                    new SocketMessage<>(SocketMessageType.COMMENT, chatMessage),
                    po.getUserId(),
                    sourceAuthor.getUserId()
            );
        }

        // 查询发布者信息
        UserSimpleInfo sender = commentMapper.selectUserById(po.getUserId());
        // 查询评论层级
        Integer level = commentMapper.getCommentLevel(comments.getId());

        CommentListVo commentListVo = CommentListVo.builder()
                .commentId(comments.getId())
                .userId(po.getUserId())
                .userName(sender.getUserName())
                .userAvatar(sender.getUserImage())
                .content(po.getContent())
                .createTime(comments.getCreateTime())
                .childCount(0L)
                .likeCount(0L)
                .isLike(false)
                .level(level)
                .build();


        return AjaxResult.success("评论成功", commentListVo);
    }

    @Override
    public AjaxResult deleteComment(Long commentId) {
        try {
            Comments comments = new Comments();
            comments.setId(commentId);
            comments.setStatus(CommentStatus.DELETED.getCode());
            commentMapper.updateById(comments);
        } catch (Exception e) {
            log.error("删除评论失败：{}", e.getMessage());
            throw new CloudBlogException("删除评论失败: " + e.getMessage(), CommonError.INTERNAL_ERROR);
        }
        return AjaxResult.success("删除成功");
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
