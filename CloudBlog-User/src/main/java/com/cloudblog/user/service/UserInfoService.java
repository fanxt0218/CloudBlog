package com.cloudblog.user.service;

import com.cloudblog.common.pojo.Po.*;
import com.cloudblog.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService {

    AjaxResult getUserInfo(Long userId);

    AjaxResult getUserAchievement(Long userId);

    AjaxResult getPersonalInfo(Long userId);

    AjaxResult getAccountSettings(Long userId);

    AjaxResult getLikeList(UserLikeListPo po);

    AjaxResult getCollectList(UserCollectListPo po);

    AjaxResult getBrowseHistory(UserBrowseListPo po);

    AjaxResult getUserFavorites(Long userId);

    AjaxResult getInterestInfo(Long userId);

    AjaxResult getCategoryInfo(Long userId);

    AjaxResult getUserPostList(Long userId, String cursor, Integer size, String sortBy, String tag);

    AjaxResult getUserShareList(Long userId, String cursor, Integer size, String sortBy, String tag);

    AjaxResult getUserLevelInfo(Long userId);

    AjaxResult updatePersonalInfo(UpdatePersonalInfoPo po);

    AjaxResult updatePassword(UpdatePasswordPo po);

    AjaxResult updatePhone(UpdatePhonePo po);

    AjaxResult updateEmail(UpdateEmailPo po);

    AjaxResult uploadAvatar(MultipartFile file);

    AjaxResult removeInterest(RemoveInterestPo po);
}
