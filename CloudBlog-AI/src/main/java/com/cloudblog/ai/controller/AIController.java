package com.cloudblog.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final ChatClient chatClient;

    public AIController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.defaultSystem("你是一个AI助手").build();
    }

    @GetMapping("/test")
    public Flux test(@RequestParam(required = false) String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
