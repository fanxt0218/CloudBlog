package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Po.RemoveInterestPo;
import com.cloudblog.common.result.AjaxResult;

import java.util.List;

public interface InterestService {

    /**
     * 获取用户兴趣信息
     * @param userId
     * @return
     */
    AjaxResult getInterestInfo(Long userId);

    /**
     * 升级用户兴趣信息
     * @param interests
     */
    void upgradeUserInterest(List<UserInterest> interests);

    AjaxResult getTagList(Integer classId);

    AjaxResult getTagClassList();

    /**
     * 移除用户兴趣
     * @param po
     */
    void removeInterest(RemoveInterestPo po);
}
