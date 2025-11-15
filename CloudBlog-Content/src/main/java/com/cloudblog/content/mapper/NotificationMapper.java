package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Notification;
import com.cloudblog.common.pojo.DoMain.NotificationType;
import com.cloudblog.common.pojo.DoMain.UserInfo;
import com.cloudblog.common.pojo.Dto.UserChatList;
import com.cloudblog.common.pojo.Dto.UserCommentList;
import com.cloudblog.common.pojo.Dto.UserFanNoticeList;
import com.cloudblog.common.pojo.Dto.UserLikeAndCollectNoticeList;
import com.cloudblog.common.pojo.Po.ReadNotificationPo;
import com.cloudblog.common.pojo.Po.UserChatDetailPo;
import com.cloudblog.common.pojo.Vo.UserChatDetailVo;
import org.apache.ibatis.annotations.Param;

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
     * 获取粉丝通知列表
     * @param userId
     * @return
     */
    List<UserFanNoticeList> getFanList(Long userId);

    /**
     * 获取点赞和收藏列表
     * @param userId
     * @return
     */
    List<UserLikeAndCollectNoticeList> getLikeAndCollectList(@Param("userId") Long userId);

    /**
     * 获取聊天用户详情
     * @return
     */
    UserChatDetailVo.ConversationInfo getChatUserInfo(@Param("userId") Long userId);

    /**
     * 获取聊天详情
     * @param po
     * @return
     */
    List<UserChatDetailVo.ChatMessage> getChatDetail(@Param("po") UserChatDetailPo po);

    /**
     * 读取通知
     * @param po
     */
    void readNotification(@Param("po") ReadNotificationPo po, @Param("type") Integer type);

    /**
     * 获取作者id
     * @param targetId
     * @param type
     * @return
     */
    UserInfo getauthorId(@Param("targetId") Long targetId, @Param("type") Integer type);

    /**
     * 获取用户信息
     */
    UserInfo getUserInfoById(@Param("userId") Long userId);
}
