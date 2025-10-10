package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class UserNotificationsPo {

    private Long userId;

    private Integer notificationId;

    private String sortBy;
}
