package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.Vo.UserBrowseListVo;
import com.cloudblog.common.pojo.Vo.UserCollectListVo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;
import com.cloudblog.common.pojo.Vo.UserPostVo;
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
            @Param("postType") Integer postType);

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
}
