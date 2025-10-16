package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.InterestMapper;
import com.cloudblog.content.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestServiceImpl implements InterestService {

    @Autowired
    private InterestMapper interestMapper;

    @Override
    public AjaxResult getInterestInfo(Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }

        // 获取用户兴趣
        List<Tag> tags = interestMapper.getUserInterest(userId);
        return AjaxResult.success(tags);
    }

    @Override
    public void upgradeUserInterest(List<UserInterest> interests) {
        if (interests == null || interests.isEmpty()) {
            return;
        }
        interestMapper.upgradeUserInterest(interests);
    }
}
