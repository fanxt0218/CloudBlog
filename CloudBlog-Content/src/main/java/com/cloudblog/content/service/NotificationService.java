package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.NotificationType;
import com.cloudblog.common.pojo.Po.UserNotificationsPo;
import com.cloudblog.common.result.AjaxResult;

import java.util.List;

public interface NotificationService {

    AjaxResult getNotificationList(UserNotificationsPo po);

    List<NotificationType> getNotificationTypeList();
}
