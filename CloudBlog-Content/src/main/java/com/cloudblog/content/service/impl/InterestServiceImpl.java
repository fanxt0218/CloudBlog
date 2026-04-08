package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.Tag;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Po.AddInterestPo;
import com.cloudblog.common.pojo.Po.RemoveInterestPo;
import com.cloudblog.common.pojo.Vo.IndexTopicVo;
import com.cloudblog.common.pojo.Vo.TagClassVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.InterestMapper;
import com.cloudblog.content.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public AjaxResult getTagList(Integer classId, String tagName, Integer pageNum, Integer pageSize) {
        boolean isSearch = pageNum != null || pageSize != null;
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        if (!isSearch) {
            if (classId != null) {
                queryWrapper.eq(Tag::getClassId, classId).eq(Tag::getStatus, 0);
            } else {
                queryWrapper = null;
            }
            List<Tag> tags = interestMapper.selectList(queryWrapper);
            return AjaxResult.success(tags);
        } else {
            pageNum = (pageNum == null || pageNum <= 0) ? 1 : pageNum;
            pageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
            Page<Tag> page = new Page<>(pageNum, pageSize);
            IPage<Tag> list = interestMapper.getTagList(page, tagName, classId);
            return AjaxResult.success(list);
        }
    }

    @Override
    public AjaxResult getTagClassList(String className, Integer pageNum, Integer pageSize) {
        boolean isSearch = pageNum != null || pageSize != null;
        pageNum = (pageNum == null || pageNum <= 0) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        Page<TagClassVo> page = new Page<>(pageNum, pageSize);
        interestMapper.getTagClassList(page, className);
        if (isSearch) {
            return AjaxResult.success(page);
        } else {
            return AjaxResult.success(page.getRecords());
        }
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
        if (tagIds == null) {
            tagIds = List.of();
        }

        // 查询原有标签
        List<Tag> existingTags = interestMapper.getPostTagInfo(id);
        List<Long> existingTagIds = existingTags.stream()
                .map(Tag::getId)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();

        // 计算需要新增的标签（在新列表中但不在原有列表中）
        List<Long> toAdd = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .toList();

        // 计算需要删除的标签（在原有列表中但不在新列表中）
        List<Long> finalTagIds = tagIds;
        List<Long> toRemove = existingTagIds.stream()
                .filter(tagId -> !finalTagIds.contains(tagId))
                .toList();

        // 执行新增操作
        if (!toAdd.isEmpty()) {
            interestMapper.addPostTag(toAdd, id);
        }

        // 执行删除操作
        if (!toRemove.isEmpty()) {
            interestMapper.removePostTag(toRemove, id);
        }
    }

    @Override
    public List<Tag> getPostTagInfo(Long postId) {
        return interestMapper.getPostTagInfo(postId);
    }
}
