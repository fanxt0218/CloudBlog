package com.cloudblog.content.controller;

import com.cloudblog.common.pojo.Po.ChatPo;
import com.cloudblog.common.pojo.Po.ReadNotificationPo;
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
     * 获取通知列表
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

    /**
     * 发送消息
     * @param po
     * @return
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody ChatPo po) {
        return notificationService.chat(po);
    }

    /**
     * 已读消息
     */
    @PostMapping("/readNotification")
    public AjaxResult readNotification(@RequestBody ReadNotificationPo po) {
        return notificationService.readNotification(po, com.cloudblog.common.enums.NotificationType.CHAT);
    }

    /**
     * 获取在线状态
     */
    @GetMapping("/getOnlineStatus")
    public AjaxResult getOnlineStatus(@RequestParam("targetUserId") Long targetUserId) {
        return notificationService.getOnlineStatus(targetUserId);
    }

    /**
     * 已读消息(评论)
     */
    @PostMapping("/readCommentNotification")
    public AjaxResult readCommentNotification(@RequestBody ReadNotificationPo po) {
        return notificationService.readNotification(po, com.cloudblog.common.enums.NotificationType.COMMENT);
    }

    /**
     * 已读消息(新增粉丝)
     */
    @PostMapping("/readFanNotification")
    public AjaxResult readFanNotification(@RequestBody ReadNotificationPo po) {
        return notificationService.readNotification(po, com.cloudblog.common.enums.NotificationType.NEW_FAN);
    }

    /**
     * 已读消息(点赞和收藏)
     */
    @PostMapping("/readLikeAndCollectNotification")
    public AjaxResult readLikeAndCollectNotification(@RequestBody ReadNotificationPo po) {
        return notificationService.readNotification(po, com.cloudblog.common.enums.NotificationType.NEW_LIKE, com.cloudblog.common.enums.NotificationType.NEW_COLLECT);
    }
}
