package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class CreateNewFavoritesPo {

    private Long userId;

    private String name;

    private String description;
}
