package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.enums.SocketMessageType;
import com.cloudblog.common.exception.CloudBlogException;
import com.cloudblog.common.pojo.DoMain.ChatMessage;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.DoMain.NotificationType;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.*;
import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.pojo.Vo.UserChatDetailVo;
import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.NotificationMapper;
import com.cloudblog.content.service.FocusService;
import com.cloudblog.content.service.NotificationService;
import com.cloudblog.content.socket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private FocusService focusService;
    @Autowired
    private WebSocket webSocket;

    @Override
    public AjaxResult getNotificationList(UserNotificationsPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        List notifications = switch (po.getNotificationId()) {
            case 1 -> getChatList(po.getUserId(), po.getSortBy()); // 聊天列表
            case 2 -> getCommentList(po.getUserId(), po.getSortBy());  // 评论列表
            case 3 -> getFanList(po.getUserId(), po.getSortBy());  // 粉丝列表
            case 4 -> getLikeAndCollectList(po.getUserId(), po.getSortBy()); // 点赞和收藏列表
            case 5 -> getLikeAndCollectList(po.getUserId(), po.getSortBy());
            default -> getChatList(po.getUserId(), po.getSortBy()); // 默认聊天列表
        };
        return AjaxResult.success(notifications);
    }


    @Override
    public List<NotificationType> getNotificationTypeList() {
        return notificationMapper.getNotificationTypeList();
    }

    @Override
    public AjaxResult getChatDetail(UserChatDetailPo po) {
        if (po.getUserId() == null || po.getTargetUserId() == null) {
            return AjaxResult.error("参与用户ID不能为空");
        }
        UserChatDetailVo userChatDetailVo = new UserChatDetailVo();
        // 查询聊天用户信息
        ArrayList<UserChatDetailVo.ConversationInfo> participants = new ArrayList<>();

        UserChatDetailVo.ConversationInfo chatUserInfo = notificationMapper.getChatUserInfo(po.getUserId());
        if (chatUserInfo == null) {
            return AjaxResult.error("用户不存在");
        }
        chatUserInfo.setSelf(true);
        participants.add(chatUserInfo);
        // 非文件传输助手
        if (!po.getTargetUserId().equals(po.getUserId())) {
            UserChatDetailVo.ConversationInfo targetUserInfo = notificationMapper.getChatUserInfo(po.getTargetUserId());
            targetUserInfo.setSelf(false);
            participants.add(targetUserInfo);
        }

        userChatDetailVo.setParticipants(participants);

        // 查询聊天记录
        List<UserChatDetailVo.ChatMessage> messages = notificationMapper.getChatDetail(po);
        userChatDetailVo.setMessages(messages);

        return AjaxResult.success(userChatDetailVo);
    }

    @Override
    public List<ChatContentType> getChatContentType() {
        // TODO 这里是暂时硬编码的内容类型
        return List.of(
                new ChatContentType(3, "文本"),
                new ChatContentType(4, "图片"),
                new ChatContentType(5, "文件"),
                new ChatContentType(6, "视频"),
                new ChatContentType(7, "音频")
                );
    }

    @Override
    public AjaxResult chat(ChatPo po) {
        if (po.getUserId() == null || po.getTargetId() == null) {
            log.info("userId{},targetId{}", po.getUserId(), po.getTargetId());
            return AjaxResult.error("参数错误");
        }
        if (po.getContent() == null || po.getContent().isEmpty()) {
            return AjaxResult.error("聊天内容不能为空");
        }
        if (notificationMapper.getChatUserInfo(po.getUserId()) == null || notificationMapper.getChatUserInfo(po.getTargetId()) == null) {
            return AjaxResult.error("目标用户不存在");
        }
        try {
            Notification notification = new Notification();
            notification.setRecipientId(po.getTargetId());
            notification.setSenderId(po.getUserId());
            notification.setContent(po.getContent());
            notification.setType(com.cloudblog.common.enums.NotificationType.CHAT.getValue());
            notification.setObjectType(po.getContentType()); // 目前默认为文本，后续可能支持其他类型
            notification.setIsRead(0);
            notification.setCreateTime(LocalDateTime.now());
            notificationMapper.insert(notification);

            // 主动推送消息(不是自己时才推送)
            if (!po.getUserId().equals(po.getTargetId())) {
                UserChatDetailVo.ChatMessage chatMessage = new UserChatDetailVo.ChatMessage();
                chatMessage.setMessageId(notification.getId());
                chatMessage.setContent(po.getContent());
                chatMessage.setSenderId(po.getUserId());
                chatMessage.setSendTime(notification.getCreateTime());
                chatMessage.setContentType(po.getContentType());
                webSocket.sendMessage(
                        new SocketMessage<>(SocketMessageType.CHAT, chatMessage),
                        po.getUserId(),
                        po.getTargetId());
            }
        } catch (Exception e) {
            log.error("发送失败：{}", e.getMessage());
            throw new CloudBlogException("发送失败");
        }
        return AjaxResult.success("发送成功");
    }

    @Override
    public AjaxResult readNotification(ReadNotificationPo po, com. cloudblog. common. enums.NotificationType... notificationTypes) {
        if (po.getUserId() == null || po.getTargetUserId() == null) {
            return AjaxResult.error("参数错误");
        }
        for (com.cloudblog.common.enums.NotificationType notificationType : notificationTypes) {
            notificationMapper.readNotification(po, notificationType.getValue());
        }
        return AjaxResult.success("success");
    }

    @Override
    public AjaxResult getOnlineStatus(Long targetUserId) {
        return webSocket.getOnlineStatus(targetUserId)?AjaxResult.success("成功","在线"):AjaxResult.success("成功","离线");
    }

    @Override
    public void focusUser(FocusUserPo po) {
        Notification notification = new Notification();
        notification.setSenderId(po.getUserId());
        notification.setRecipientId(po.getFocusUserId());
        notification.setType(com.cloudblog.common.enums.NotificationType.NEW_FAN.getValue());
        notification.setContent("关注了你");
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public void likeNotification(Long userId, Long targetId, Integer status, Integer type) {
        // 读取同种类型点赞未读数据
        Notification notification = notificationMapper.selectOne(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getType, com.cloudblog.common.enums.NotificationType.NEW_LIKE.getValue())
                        .eq(Notification::getObjectType, type)
                        .eq(Notification::getObjectId, targetId)
                        .eq(Notification::getIsRead, 0)
        );

        UserInfo userInfo = notificationMapper.getauthorId(targetId, type);
        boolean isNeedInsert = false;

        if (notification != null){
            // 被点赞过
            if (status == 0) {
                // 未读
                Integer isAggregated = notification.getIsAggregated();
                if (isAggregated == null || isAggregated == 0) {
                    // 未聚合，执行聚合操作
                    notification.setIsAggregated(1);
                    notification.setAggregatedCount(1);
                }
                notification.setAggregatedCount(notification.getAggregatedCount() + 1);
                // 查找本次点赞人
                UserInfo userInfoById = notificationMapper.getUserInfoById(userId);
                notification.setContent(
                        "、"+userInfoById.getUserName()+
                        " 等"+notification.getAggregatedCount()+"人点赞了你的"+
                        switch (type){
                            case 0 -> "文章";
                            case 1 -> "动态";
                            case 2 -> "评论";
                            default -> "内容";
                        });
                notification.setUpdateTime(LocalDateTime.now());
                notificationMapper.updateById(notification);
            }else {
                // 已读,执行新增
                isNeedInsert = true;
            }
        } else {
            // 没有被点赞过
            isNeedInsert = true;
        }

        if (isNeedInsert) {
            // 查找目标用户信息
            if (userInfo == null) {
                return;
            }
            Notification tempNotification = new Notification();
            tempNotification.setSenderId(userId);
            tempNotification.setRecipientId(userInfo.getUserId());
            tempNotification.setType(com.cloudblog.common.enums.NotificationType.NEW_LIKE.getValue());
            tempNotification.setObjectType(type);
            tempNotification.setObjectId(targetId);
            tempNotification.setContent("点赞了你的" + switch (type) {
                case 0 -> "文章";
                case 1 -> "动态";
                case 2 -> "评论";
                default -> "内容";
            });
            tempNotification.setCreateTime(LocalDateTime.now());
            tempNotification.setUserName(userInfo.getUserName());

            notificationMapper.insert(tempNotification);
        }

    }

    @Override
    public void collectNotification(Long userId, Long postId, Integer status) {
        // 读取同种类型点赞未读数据
        Notification notification = notificationMapper.selectOne(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getType, com.cloudblog.common.enums.NotificationType.NEW_COLLECT.getValue())
                        .eq(Notification::getObjectType, ContentType.POST.ordinal())
                        .eq(Notification::getObjectId, postId)
                        .eq(Notification::getIsRead, 0)
        );

        // 查找目标用户信息
        UserInfo userInfo = notificationMapper.getauthorId(postId, ContentType.POST.ordinal());
        boolean isNeedInsert = false;

        if (notification != null){
            // 被点赞过
            if (status == 0) {
                // 未读
                Integer isAggregated = notification.getIsAggregated();
                if (isAggregated == null || isAggregated == 0) {
                    // 未聚合，执行聚合操作
                    notification.setIsAggregated(1);
                    notification.setAggregatedCount(1);
                }
                notification.setAggregatedCount(notification.getAggregatedCount() + 1);
                notification.setUpdateTime(LocalDateTime.now());
                // 查找本次点赞人
                UserInfo userInfoById = notificationMapper.getUserInfoById(userId);
                notification.setContent("、"+userInfoById.getUserName()+" 等"+notification.getAggregatedCount()+"人收藏了你的文章");
                notificationMapper.updateById(notification);
            }else {
                // 已读,执行新增
                isNeedInsert = true;
            }
        } else {
            // 没有被点赞过
            isNeedInsert = true;
        }

        if (isNeedInsert) {
            if (userInfo == null) {
                return;
            }
            Notification tempNotification = new Notification();
            tempNotification.setSenderId(userId);
            tempNotification.setRecipientId(userInfo.getUserId());
            tempNotification.setType(com.cloudblog.common.enums.NotificationType.NEW_COLLECT.getValue());
            tempNotification.setObjectType(ContentType.POST.ordinal());
            tempNotification.setObjectId(postId);
            tempNotification.setContent("收藏了你的文章");
            tempNotification.setCreateTime(LocalDateTime.now());
//            tempNotification.setUserName(userInfo.getUserName());

            notificationMapper.insert(tempNotification);
        }

    }

    @Override
    public Long addNotification(Notification notification) {
        return notificationMapper.insert(notification);
    }

    /**
     * 获取聊天列表
     * @param userId
     * @param sortBy
     * @return
     */
    private List<UserChatList> getChatList(Long userId, String sortBy) {
        List<UserChatList> chatList = notificationMapper.getChatList(userId);
        // 判断关系
        judgeRelationship(chatList, UserChatList.class, userId);
        return chatList;
    }

    /**
     * 获取评论列表
     * @param userId
     * @param sortBy
     * @return
     */
    private List<UserCommentList> getCommentList(Long userId, String sortBy) {
        return notificationMapper.getCommentList(userId);
    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param sortBy
     * @return
     */
    private List<UserFanNoticeList> getFanList(Long userId, String sortBy) {
        return notificationMapper.getFanList(userId);
    }

    /**
     * 获取点赞和收藏列表
     * @param userId
     * @param sortBy
     * @return
     */
    private List<UserLikeAndCollectNoticeList> getLikeAndCollectList(Long userId, String sortBy) {
        List<UserLikeAndCollectNoticeList> likeAndCollectList = notificationMapper.getLikeAndCollectList(userId);
        // 判断关系
        judgeRelationship(likeAndCollectList, UserLikeAndCollectNoticeList.class, userId);
        return likeAndCollectList;
    }

    /**
     * 判断关系
     * @param srcList
     * @param type
     */
    private <T> void judgeRelationship(List<T> srcList, Class<T> type, Long userId) {
        // 获取用户粉丝列表
        List<UserFanListVo> userFanList = focusService.getUserFanList(userId);
        // TODO 特殊角色，暂时硬编码
        List<Long> specialRoleList = List.of(1L);
        if (!srcList.isEmpty()) {
            if (type == UserChatList.class) { // 聊天列表
                // 获取用户关注列表
                List<UserFocusListVo> userFocusList = focusService.getUserFocusList(userId);
                for (T chat : srcList) {
                    if (chat == null) {
                        continue;
                    }
                    UserChatList singleChat = (UserChatList) chat;
                    // 特殊角色
                    if (specialRoleList.contains(singleChat.getUserId())){
                        singleChat.setRelationship("官方");
                    }
                    if (singleChat.getRelationship() == null) {
                        // 判断关系(是否是粉丝/互相关注)
                        userFanList.forEach(focus -> {
                            if (focus.getUserId().equals(singleChat.getUserId())) {
                                if (focus.getIsFollowEachOther().equals(1)) {
                                    singleChat.setRelationship("好友");
                                } else {
                                    singleChat.setRelationship("粉丝");
                                }
                                return;
                            }
                        });
                    }
                    if (singleChat.getRelationship() == null) {
                        // 判断关系(是否关注)
                        userFocusList.forEach(focus -> {
                            if (focus.getUserId().equals(singleChat.getUserId())) {
                                singleChat.setRelationship("已关注");
                                return;
                            }
                        });
                    }
                    if (singleChat.getRelationship() == null) {
                        if (singleChat.getUserId().equals(userId)) {
                            singleChat.setRelationship("自己");
                        }else {
                            singleChat.setRelationship("陌生人");
                        }
                    }
                }
            } else if (type == UserLikeAndCollectNoticeList.class) { // 点赞和收藏列表
                for (T likeAndCollect : srcList) {
                    if (likeAndCollect == null) {
                        continue;
                    }
                    UserLikeAndCollectNoticeList singleLikeAndCollect = (UserLikeAndCollectNoticeList) likeAndCollect;
                    // 判断关系(是否是粉丝)
                    if (userFanList.stream().anyMatch(fan -> fan.getUserId().equals(singleLikeAndCollect.getUserId()))) {
                        singleLikeAndCollect.setRelationship("粉丝");
                    }
                }
            }
        }
    }

    /**
     * 获取通知对象类型中文名
     */
    public String getNotificationTypeList(Integer type) {
        switch (type){
            case 1:
                return "聊天";
            case 2:
                return "评论";
            case 3:
                return "粉丝";
            case 4:
                return "点赞";
            case 5:
                return "收藏";
            default:
                return "未知";
        }
    }

}
