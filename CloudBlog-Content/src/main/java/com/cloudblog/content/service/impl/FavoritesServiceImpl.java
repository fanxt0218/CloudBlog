package com.cloudblog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudblog.common.pojo.DoMain.Favorites;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.content.mapper.FavoritesMapper;
import com.cloudblog.content.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    private FavoritesMapper favoritesMapper;

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
}
