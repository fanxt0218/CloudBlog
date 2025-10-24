package com.cloudblog.content.service.impl;

import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.LikeMapper;
import com.cloudblog.content.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public AjaxResult liking(Long userId, Long targetId, Integer status ,Integer type) {
        likeMapper.liking(userId, targetId, status, type);
        // TODO 通知、兴趣权重
        return AjaxResult.success("操作成功");
    }

    @Override
    public Integer getUserLikeCount(Long userId) {
        return likeMapper.getUserLikeCount(userId);
    }
}
