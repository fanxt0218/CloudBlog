package com.cloudblog.common.pojo.Po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FocusUserPo {

    private Long userId;

    private Long focusUserId;

    /**
     * 0: 关注
     * 1: 取消关注
     */
    private Integer status;

    private String source;
}
