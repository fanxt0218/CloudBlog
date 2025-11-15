package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.NotificationType;
import com.cloudblog.common.pojo.Dto.ChatContentType;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.result.AjaxResult;

import java.util.List;

public interface NotificationService {

    AjaxResult getNotificationList(UserNotificationsPo po);

    List<NotificationType> getNotificationTypeList();

    AjaxResult getChatDetail(UserChatDetailPo po);

    List<ChatContentType> getChatContentType();

    AjaxResult chat(ChatPo po);

    AjaxResult readNotification(ReadNotificationPo po, com.cloudblog.common.enums.NotificationType... type);

    AjaxResult getOnlineStatus(Long targetUserId);

    /**
     * 关注用户,增加关注通知
     * @param po
     */
    void focusUser(FocusUserPo po);

    /**
     * 点赞,增加点赞通知
     * @param userId
     * @param targetId
     * @param type
     */
    void likeNotification(Long userId, Long targetId, Integer status, Integer type);

    /**
     * 收藏,增加收藏通知
     * @param userId
     * @param postId
     * @param status
     */
    void collectNotification(Long userId, Long postId, Integer status);
}
