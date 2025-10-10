package com.cloudblog.common.pojo.Vo;

import com.cloudblog.common.enums.ContentType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserChatDetailVo {

    private List<ConversationInfo> participants;

    private List<ChatMessage> messages;

    /**
     * 会话信息
     */
    @Data
    public static class ConversationInfo {

        private Long userId;

        private String userName;

        private String userImage;

        private boolean isSelf;
    }

    /**
     * 聊天信息
     */
    @Data
    public static class ChatMessage {

        private Long messageId;

        private Long senderId;

        private LocalDateTime sendTime;

        private Integer contentType;

        private String content;
    }
}
