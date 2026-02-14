package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class CreateAssistPo {

    private String message;

    private String content;

    private String type;

    private String currentConversation;

    private String title;
}
