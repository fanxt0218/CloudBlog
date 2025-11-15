package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.Likes;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.LikeMapper;
import com.cloudblog.content.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private NotificationServiceImpl notificationService;

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
            notificationService.likeNotification(userId, targetId, status, type);
            // TODO 兴趣权重
        }
        return AjaxResult.success("操作成功");
    }

    @Override
    public Integer getUserLikeCount(Long userId) {
        return likeMapper.getUserLikeCount(userId);
    }
}
