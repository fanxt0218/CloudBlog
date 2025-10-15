package com.cloudblog.ai.controller;

import com.cloudblog.ai.service.AiService;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.common.utils.SystemPromptGenerator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AiService aiService;

    private final ChatClient chatClient;

    public AIController(ChatClient.Builder chatClient, VectorStore vectorStore, ChatMemory chatMemory) {
        this.chatClient = chatClient
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .defaultSystem(SystemPromptGenerator.generateSystemPrompt()).build();
    }

    @PostMapping("/chat")
    public Flux<String> test(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String conversationId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        // 判断会话是否存在，不存在则创建会话
        aiService.initConversation(userId, conversationId);

        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }


    /**
     * 获取会话列表
     * @param userId
     * @return
     */
    @GetMapping("/chatList")
    public AjaxResult chatList(@RequestParam Long userId) {
        return AjaxResult.success(aiService.getChatList(userId));
    }

    /**
     * 获取会话详情
     * @param conversationId
     * @return
     */
    @GetMapping("/chatDetail")
    public AjaxResult getChatDetail(@RequestParam String conversationId) {
        return AjaxResult.success(aiService.getChatDetail(conversationId));
    }
}
