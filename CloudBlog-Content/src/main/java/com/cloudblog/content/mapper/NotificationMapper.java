package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.DoMain.NotificationType;
import com.cloudblog.common.pojo.Dto.UserChatList;
import com.cloudblog.common.pojo.Dto.UserCommentList;
import com.cloudblog.common.pojo.Dto.UserFanNoticeList;
import com.cloudblog.common.pojo.Dto.UserLikeAndCollectNoticeList;

import java.util.List;

public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 获取通知类型列表
     * @return
     */
    List<NotificationType> getNotificationTypeList();

    /**
     * 获取聊天列表
     * @param userId
     * @return
     */
    List<UserChatList> getChatList(Long userId);

    /**
     * 获取评论列表
     * @param userId
     * @return
     */
    List<UserCommentList> getCommentList(Long userId);

    /**
     * 获取粉丝列表
     * @param userId
     * @return
     */
    List<UserFanNoticeList> getFanList(Long userId);

    /**
     * 获取点赞和收藏列表
     * @param userId
     * @return
     */
    List<UserLikeAndCollectNoticeList> getLikeAndCollectList(Long userId);
}
