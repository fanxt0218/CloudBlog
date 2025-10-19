package com.cloudblog.ai.service;

import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {

    /**
     * 初始化会话
     * @param conversationId
     */
    void initConversation(Long userId, String conversationId);

    /**
     * 获取会话列表
     * @param userId
     * @return
     */
    AjaxResult getChatList(Long userId);

    /**
     * 获取会话详情
     * @param conversationId
     * @return
     */
    AjaxResult getChatDetail(String conversationId);

    /**
     * 删除会话
     * @param conversationId
     * @return
     */
    AjaxResult deleteChat(String conversationId);

    /**
     * 保存用户消息(简洁版)
     * @param userId
     * @param conversationId
     * @param message
     * @param filePath
     */
    void saveUserMessage(Long userId, String conversationId, String message, String filePath);

    /**
     * 保存AI消息(简洁版)
     * @param userId
     * @param conversationId
     * @param content
     */
    void saveAssistantMessage(Long userId, String conversationId, String content);

    /**
     * 上传文件
     * @param file
     * @return
     */
    String uploadFile(Long userId, String conversationId, MultipartFile file);
}
