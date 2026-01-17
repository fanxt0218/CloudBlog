package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Po.AddInterestPo;
import com.cloudblog.common.pojo.Po.RemoveInterestPo;
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

    @Override
    public AjaxResult getTagList(Integer classId) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        if (classId != null) {
            queryWrapper.eq(Tag::getClassId, classId);
        } else {
            queryWrapper = null;
        }
        List<Tag> tags = interestMapper.selectList(queryWrapper);
        return AjaxResult.success(tags);
    }

    @Override
    public AjaxResult getTagClassList() {
        return AjaxResult.success(interestMapper.getTagClassList());
    }

    @Override
    public void removeInterest(RemoveInterestPo po) {
        interestMapper.removeUserInterest(po);
    }

    @Override
    public void addInterest(AddInterestPo po) {
        // 判断标签是否存在
        if (interestMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getId, po.getTagId())) == null) {
            return;
        }
        // 判断是否已经添加过
        List<Tag> userInterest = interestMapper.getUserInterest(po.getUserId());
        if (userInterest.stream().anyMatch(tag -> tag.getId().equals(po.getTagId()))) {
            return;
        }

        interestMapper.addUserInterest(po);
    }

    @Override
    public void addPostTag(List<Long> tagIds, Long id) {
        interestMapper.addPostTag(tagIds, id);
    }

    @Override
    public List<Tag> getPostTagInfo(Long postId) {
        return interestMapper.getPostTagInfo(postId);
    }
}
