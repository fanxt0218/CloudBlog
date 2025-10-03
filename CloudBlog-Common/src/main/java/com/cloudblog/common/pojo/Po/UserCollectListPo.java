package com.cloudblog.common.pojo.Po;

import lombok.Data;

@Data
public class UserCollectListPo {

    private Long userId;

    private Integer favoritesId;

    private String postName;

    private Integer pageNum;

    private Integer pageSize;
}
