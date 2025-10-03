package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Favorites;

public interface FavoritesMapper extends BaseMapper<Favorites> {

    /**
     * 初始化用户收藏夹
     * @param id
     */
    void initDefaultFavorites(Long id);
}
