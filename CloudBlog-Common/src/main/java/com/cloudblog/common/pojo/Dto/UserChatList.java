package com.cloudblog.common.pojo.Dto;

import lombok.Data;

@Data
public class UserChatList {

    private Long userId;

    private String userName;

    private String userImage;

    private String lastMessage;

    private String lastMessageTime;

    private String relationship;

    private Integer unreadCount;
}
