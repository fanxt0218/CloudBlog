package com.cloudblog.user.service;

import com.cloudblog.common.pojo.Po.UserCollectListPo;
import com.cloudblog.common.pojo.Po.UserLikeListPo;
import com.cloudblog.common.result.AjaxResult;

public interface UserInfoService {

    AjaxResult getUserInfo(Long userId);

    AjaxResult getUserAchievement(Long userId);

    AjaxResult getPersonalInfo(Long userId);

    AjaxResult getAccountSettings(Long userId);

    AjaxResult getLikeList(UserLikeListPo po);

    AjaxResult getCollectList(UserCollectListPo po);
}
