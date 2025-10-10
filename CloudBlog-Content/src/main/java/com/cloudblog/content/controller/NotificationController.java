package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.UserChatDetailPo;
import com.cloudblog.common.pojo.Po.UserNotificationsPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取通知类型列表
     */
    @GetMapping("/getNotificationTypeList")
    public AjaxResult getNotificationTypeList() {
        return AjaxResult.success(notificationService.getNotificationTypeList());
    }

    /**
     * 获取通知列表(聊天信息)
     */
    @PostMapping("/getNotificationList")
    public AjaxResult getChatList(@RequestBody UserNotificationsPo po) {
        return notificationService.getNotificationList(po);
    }

    /**
     * 获取聊天详情
     */
    @PostMapping("/getChatDetail")
    public AjaxResult getChatDetail(@RequestBody UserChatDetailPo po) {
        return notificationService.getChatDetail(po);
    }

    /**
     * 获取聊天内容类型
     */
    @GetMapping("/getChatContentType")
    public AjaxResult getChatContentType() {
        return AjaxResult.success(notificationService.getChatContentType());
    }
}
