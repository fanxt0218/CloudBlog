package com.cloudblog.content.service.impl;

import com.cloudblog.common.enums.ContentType;
import com.cloudblog.common.pojo.DoMain.Likes;
import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.LikeMapper;
import com.cloudblog.content.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private NotificationServiceImpl notificationService;
    @Autowired
    private InterestServiceImpl interestService;

    @Transactional
    @Override
    public AjaxResult liking(Long userId, Long targetId, Integer status ,Integer type) {
        if (status == 1) {
            // 取消点赞
            likeMapper.liking(userId, targetId, status, type);
        } else if (status == 0){
            // 点赞
            Likes likes = new Likes();
            likes.setUserId(userId);
            likes.setTargetId(targetId);
            likes.setType(type);
            likes.setCreateTime(LocalDateTime.now());
            likeMapper.insert(likes);
            // 通知
            // 判断目标是否是自己
            notificationService.likeNotification(userId, targetId, status, type);
            // 兴趣权重
            if (type.equals(ContentType.POST.ordinal())) {
                ArrayList<UserInterest> upgrades = new ArrayList<>();
                List<Tag> postTagInfo = interestService.getPostTagInfo(targetId);
                // 查找用户兴趣
                likeMapper.getUserInterests(userId).forEach(interest -> {
                    if (postTagInfo == null || postTagInfo.isEmpty()) {
                        return;
                    }
                    postTagInfo.forEach(tag -> {
                        if (tag.getId().equals(interest.getTagId())) {
                            interest.setWeight(interest.getWeight().add(new BigDecimal("0.3"))); // 权重加0.3
                            upgrades.add(interest);
                        }
                    });
                });
                if (!upgrades.isEmpty()) {
                    interestService.upgradeUserInterest(upgrades);
                }
            }
        }
        return AjaxResult.success("操作成功");
    }

    @Override
    public Integer getUserLikeCount(Long userId) {
        return likeMapper.getUserLikeCount(userId);
    }

    @Override
    public Long calculateLikeCount(Long contentId, Integer type) {
        return likeMapper.calculateLikeCount(contentId, type);
    }
}
