package com.cloudblog.content.socket;

import com.cloudblog.common.pojo.Dto.SocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/chat/{userId}")
public class WebSocket {

    private static final ConcurrentHashMap<Long, Session> webSocketMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        log.info("用户{}建立连接", userId);
        webSocketMap.put(userId, session);
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("收到消息：{}", message);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
//        log.info("WebSocket连接已关闭");
        webSocketMap.remove(userId);
    }

    /**
     * 发送消息
     * @param message
     * @param userId
     * @param targetUserId
     */
    public void sendMessage(SocketMessage message, Long userId, Long targetUserId) {
        log.info("用户{}向{}发送消息：{}", userId, targetUserId, message);
        Session session = webSocketMap.get(targetUserId);
        if (session != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(message));
            } catch (Exception e) {
                log.error("发送消息失败：{}", e.getMessage());
            }
        } else {
            log.warn("用户{}的WebSocket连接不存在", targetUserId);
        }
    }

    /**
     * 获取在线状态
     */
    public Boolean getOnlineStatus(Long targetUserId) {
        return webSocketMap.containsKey(targetUserId);
    }
}
