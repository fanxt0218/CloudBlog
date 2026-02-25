package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.pojo.DoMain.Collect;
import com.cloudblog.common.pojo.DoMain.Favorites;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Po.CreateNewFavoritesPo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.FavoritesMapper;
import com.cloudblog.content.service.FavoritesService;
import com.cloudblog.content.service.InterestService;
import com.cloudblog.content.service.NotificationService;
import jdk.jfr.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    private FavoritesMapper favoritesMapper;
    @Autowired
    @Lazy
    private NotificationService notificationService;
    @Autowired
    private InterestService interestService;

    @Override
    public AjaxResult getUserFavorites(Long userId) {
        if (userId == null) {
            return AjaxResult.warn("用户ID不能为空");
        }
        List<Favorites> favorites = favoritesMapper.selectList(new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId, userId));
        return AjaxResult.success(favorites);
    }

    @Override
    public void initDefaultFavorites(Long id) {
        favoritesMapper.initDefaultFavorites(id);
    }

    @Override
    public Favorites getUserDefaultFavorites(Long userId) {
        List<Favorites> favorites = favoritesMapper.selectList(new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId, userId));
        // 按照id排序
        favorites.sort(Comparator.comparing(Favorites::getId));
        // 返回默认收藏夹
        return favorites.get(0);
    }

    @Transactional
    @Override
    public AjaxResult collecting(Long userId, Long postId, Integer status, Integer favoriteId) {
        if (status == 1) {
            favoritesMapper.collecting(userId, postId, status, favoriteId);
        } else {
            // 收藏
            Collect collect = new Collect();
            collect.setPostId(postId);
            collect.setUserId(userId);
            collect.setCreateTime(LocalDateTime.now());
            if (favoriteId != null) {
                collect.setFavoritesId(favoriteId);
            } else {
                // 获取默认收藏夹
                Favorites favorites = getUserDefaultFavorites(userId);
                collect.setFavoritesId(favorites.getId());
            }
            favoritesMapper.addCollect(collect);
            // 通知
            notificationService.collectNotification(userId, postId, status);
            // 兴趣权重
            // 查找用户兴趣标签，与文章的标签进行匹配
            // 获取文章的标签
            List<Integer> tags = favoritesMapper.getPostTagByPostId(postId);
            List<UserInterest> upgrades = new ArrayList<>();
            favoritesMapper.getUserInterestTags(userId).forEach(tag -> {
                if (tags == null || tags.isEmpty()) {
                    return;
                }
                // 匹配标签
                tags.forEach(tagId -> {
                    if (tagId.equals(tag.getTagId())) {
                        // 添加权重
                        tag.setWeight(tag.getWeight().add(new BigDecimal("0.5"))); // 权重加0.5
                        upgrades.add(tag);
                    }
                });
            });
            if (!upgrades.isEmpty()) {
                interestService.upgradeUserInterest(upgrades);
            }
        }
        return AjaxResult.success("操作成功");
    }

    @Override
    public AjaxResult newFavorites(CreateNewFavoritesPo po) {
        if (po.getUserId() == null) {
            return AjaxResult.warn("用户ID不能为空");
        }
        if (po.getName() == null) {
            return AjaxResult.warn("收藏夹名称不能为空");
        }
        // 检查现在有几个收藏夹
        // TODO 后面可能会有收藏夹状态，要根据状态过滤
        int count = Math.toIntExact(favoritesMapper.selectCount(new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId, po.getUserId())));

        if (count >= 5) {
            return AjaxResult.warn("最多只能拥有5个收藏夹");
        }

        // 创建收藏夹
        Favorites favorites = new Favorites();
        favorites.setUserId(po.getUserId());
        favorites.setDescription(po.getDescription());
        favorites.setFavoritesName(po.getName());
        favorites.setCreateTime(LocalDateTime.now());
        favoritesMapper.insert(favorites);
        return AjaxResult.success("创建成功", favorites);
    }

    @Override
    public AjaxResult getTargetHasCollectedFavorites(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return AjaxResult.warn("用户ID或文章ID不能为空");
        }

        List<Integer> favoriteIds = favoritesMapper.getTargetHasCollectedFavorites(userId, postId);
        return AjaxResult.success(favoriteIds);
    }
}
