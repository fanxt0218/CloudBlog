package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Collect;
import com.cloudblog.common.pojo.DoMain.Favorites;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FavoritesMapper extends BaseMapper<Favorites> {

    /**
     * 初始化用户收藏夹
     * @param id
     */
    void initDefaultFavorites(Long id);

    /**
     * 收藏、取消收藏
     * @param userId
     * @param postId
     * @return
     */
    void collecting(
            @Param("userId") Long userId,
            @Param("postId") Long postId,
            @Param("status") Integer status,
            @Param("favoriteId") Integer favoriteId
    );

    /**
     * 添加收藏
     * @param collect
     */
    void addCollect(Collect collect);

    /**
     * 获取内容已被收藏的收藏夹
     * @param userId
     * @param postId
     * @return
     */
    List<Integer> getTargetHasCollectedFavorites(@Param("userId") Long userId, @Param("postId") Long postId);

    /**
     * 获取用户兴趣标签
     * @param userId
     * @return
     */
    List<UserInterest> getUserInterestTags(Long userId);

    /**
     * 获取文章标签
     *
     * @param postId
     * @return
     */
    List<Integer> getPostTagByPostId(Long postId);
}
