package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.PostTag;
import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import com.cloudblog.common.pojo.Vo.*;
import jakarta.annotation.security.PermitAll;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostMapper extends BaseMapper<Posts> {

    /**
     * 获取用户点赞文章列表
     * @param userId
     * @param beginTime
     * @param endTime
     * @param ordinal
     * @return
     */
    IPage<UserLikeListVo> getUserLikeList(
            IPage<UserLikeListVo> page,
            @Param("userId") Long userId,
            @Param("beginTime") LocalDate beginTime,
            @Param("endTime") LocalDate endTime,
            @Param("type") int ordinal
    );

    /**
     * 获取用户收藏文章列表
     * @param page
     * @param userId
     * @param postName
     * @return
     */
    IPage<UserCollectListVo> getUserCollectList(
            Page<UserCollectListVo> page,
            @Param("userId") Long userId,
            @Param("favoritesId") Integer favoritesId,
            @Param("postName") String postName
    );

    /**
     * 获取用户浏览历史
     * @param page
     * @param userId
     * @param beginTime
     * @param endTime
     * @return
     */
    IPage<UserBrowseListVo> getUserBrowseHistory(
            Page<UserBrowseListVo> page,
            @Param("userId") Long userId,
            @Param("beginTime") LocalDate beginTime,
            @Param("endTime") LocalDate endTime,
            @Param("type") int ordinal
    );

    /**
     * 获取用户文章列表
     * @param userId
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<UserPostVo> getUserPostList(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i);

    /**
     * 获取用户文章列表(根据兴趣推送)
     * @param userId
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<UserPostVo> getPostListWithInterest(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i,
            @Param("postType") Integer postType,
            @Param("lastInterestScore") Double lastInterestScore);

    /**
     * 获取用户文章列表(不根据无兴趣推送)
     * @param userId
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<UserPostVo> getPostListWithNoInterest(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i,
            @Param("tagId") Integer tagId,
            @Param("postType") Integer postType);


    /**
     * 获取文章标签
     * @param postId
     * @return
     */
    List<PostTag> getPostTagByPostId(Long postId);

    /**
     * 获取用户兴趣
     * @param userId
     * @return
     */
    List<UserInterest> getUserInterest(Long userId);

    /**
     * 增加文章浏览次数
     * @param postId
     */
    void addPostBrowseCount(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 获取用户文章总数
     * @param userId
     * @return
     */
    Long getUserTotalCount(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 获取用户文章列表(根据关注推送)
     * @param lastTargetId
     * @param lastCreateTime
     * @param i
     * @param userId
     * @return
     */
    List<IndexFocusArticleVo> getFocusPostList(@Param("lastTargetId") Long lastTargetId, @Param("lastCreateTime") LocalDateTime lastCreateTime, @Param("size") int i, @Param("userId") Long userId);
}
