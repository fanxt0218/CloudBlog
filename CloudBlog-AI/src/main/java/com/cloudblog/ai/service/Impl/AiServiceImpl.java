package com.cloudblog.ai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.ai.mapper.AiMapper;
import com.cloudblog.ai.service.AiService;
import com.cloudblog.common.pojo.DoMain.AiChat;
import com.cloudblog.common.pojo.DoMain.Conversation;
import com.cloudblog.common.pojo.Dto.AiChatDetail;
import com.cloudblog.common.pojo.Dto.AiChatList;
import com.cloudblog.common.result.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiMapper aiMapper;

    @Override
    public void initConversation(Long userId, String conversationId) {
        //判断会话是否存在
        Conversation conversation = aiMapper.getChatByConversationId(conversationId);
        //不存在则创建会话
        if (conversation == null) {
            aiMapper.createConversation(userId, conversationId);
        }
    }

    @Override
    public AjaxResult getChatList(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        List<AiChatList> chatList = aiMapper.getChatList(userId);
        return AjaxResult.success(chatList);
    }

    @Override
    public AjaxResult getChatDetail(String conversationId) {
        if (conversationId == null) {
            return AjaxResult.success("空的会话id");
        }
        List<AiChatDetail> chatDetails = aiMapper.getChatDetail(conversationId);
        return AjaxResult.success(chatDetails);
    }

    @Transactional
    @Override
    public AjaxResult deleteChat(String conversationId) {
        // 删除会话详情
        aiMapper.delete(new LambdaQueryWrapper<AiChat>().eq(AiChat::getConversationId, conversationId));
        // 删除会话
        aiMapper.deleteConversation(conversationId);
        return AjaxResult.success("删除成功");
    }

    @Override
    public void saveUserMessage(Long userId, String conversationId, String message, MultipartFile file) {
        String filePath = null;
        if (file != null) {
            // TODO 保存文件
            filePath = "";
        }
        aiMapper.saveUserMessage(userId, conversationId, message, filePath);
    }

    @Override
    public void saveAssistantMessage(Long userId, String conversationId, String content) {
        aiMapper.saveAssistantMessage(userId, conversationId, content);
    }
}
