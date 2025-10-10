package com.cloudblog.content.service;

import com.cloudblog.common.pojo.Vo.UserFanListVo;
import com.cloudblog.common.pojo.Vo.UserFocusListVo;

import java.util.List;

public interface FocusService {

    /**
     * 获取用户关注列表
     * @param userId
     * @return
     */
    List<UserFocusListVo> getUserFocusList(Long userId);

    /**
     * 获取用户粉丝列表
     * @param userId
     * @return
     */
    List<UserFanListVo> getUserFanList(Long userId);
}
