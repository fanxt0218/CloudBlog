package com.cloudblog.user.service;

import com.cloudblog.common.result.AjaxResult;

public interface UserInfoService {

    AjaxResult getUserInfo(Long userId);

    AjaxResult getUserAchievement(Long userId);
}
