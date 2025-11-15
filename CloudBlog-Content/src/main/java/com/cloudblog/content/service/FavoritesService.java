package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.Favorites;
import com.cloudblog.common.result.AjaxResult;

public interface FavoritesService {

    AjaxResult getUserFavorites(Long userId);

    /**
     * 初始化用户收藏夹
     * @param id
     */
    void initDefaultFavorites(Long id);

    Favorites getUserDefaultFavorites(Long userId);

    AjaxResult collecting(Long userId, Long postId, Integer status, Integer favoriteId);
}
