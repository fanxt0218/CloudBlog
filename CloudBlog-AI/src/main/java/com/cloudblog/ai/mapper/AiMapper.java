package com.cloudblog.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.AiChat;
import com.cloudblog.common.pojo.DoMain.Conversation;
import com.cloudblog.common.pojo.Dto.AiChatDetail;
import com.cloudblog.common.pojo.Dto.AiChatList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiMapper extends BaseMapper<AiChat> {

    /**
     * 创建会话
     * @param conversationId
     */
    void createConversation(@Param("userId") Long userId, @Param("conversationId") String conversationId);

    /**
     * 获取会话列表
     * @param userId
     * @return
     */
    List<AiChatList> getChatList(Long userId);

    /**
     * 获取会话详情
     * @param conversationId
     * @return
     */
    List<AiChatDetail> getChatDetail(String conversationId);

    /**
     * 获取会话
     * @param conversationId
     * @return
     */
    Conversation getChatByConversationId(String conversationId);
}
