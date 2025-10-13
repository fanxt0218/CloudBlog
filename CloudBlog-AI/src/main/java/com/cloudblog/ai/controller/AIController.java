package com.cloudblog.ai.controller;

import com.cloudblog.common.pojo.Po.AiChatPo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final ChatClient chatClient;

    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(50).build();

    public AIController(ChatClient.Builder chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build())
                .defaultSystem("你是一个AI助手").build();
    }

    @PostMapping("/chat")
    public Flux test(@RequestParam String message,
                     @RequestParam(value = "file", required = false) MultipartFile file) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
