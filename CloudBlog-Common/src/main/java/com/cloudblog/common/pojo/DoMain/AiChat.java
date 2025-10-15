package com.cloudblog.common.pojo.DoMain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("SPRING_AI_CHAT_MEMORY")
public class AiChat {

    private String conversationId;

    private String content;

    private String type;

    private LocalDateTime timestamp;

    private Integer autoId;
}
