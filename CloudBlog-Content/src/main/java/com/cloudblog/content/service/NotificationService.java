package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.NotificationType;
import com.cloudblog.common.pojo.Dto.ChatContentType;
import com.cloudblog.common.pojo.Po.ChatPo;
import com.cloudblog.common.pojo.Po.ReadNotificationPo;
import com.cloudblog.common.pojo.Po.UserChatDetailPo;
import com.cloudblog.common.pojo.Po.UserNotificationsPo;
import com.cloudblog.common.result.AjaxResult;

import java.util.List;

public interface NotificationService {

    AjaxResult getNotificationList(UserNotificationsPo po);

    List<NotificationType> getNotificationTypeList();

    AjaxResult getChatDetail(UserChatDetailPo po);

    List<ChatContentType> getChatContentType();

    AjaxResult chat(ChatPo po);

    AjaxResult readNotification(ReadNotificationPo po);

    AjaxResult getOnlineStatus(Long targetUserId);
}
