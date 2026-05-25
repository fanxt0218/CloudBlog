package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.*;
import com.cloudblog.common.pojo.Dto.CheckReport;
import com.cloudblog.common.pojo.Dto.ESPost;
import com.cloudblog.common.pojo.Dto.PostDataInfo;
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

    /**
     * 添加文章内容
     * @param postContent
     */
    Long insertContent(@Param("po") PostsContent postContent);

    /**
     *  获取文章分类信息
     * @param categoryId
     * @return
     */
    Category getPostCategoryInfo(Integer categoryId);

    /**
     * 获取文章内容
     * @param contentId
     * @return
     */
    PostsContent getPostContent(Long contentId);

    /**
     * 计算文章数据
     * @param id
     * @return
     */
    PostDataInfo CalculatePostData(Long id);

    /**
     * 判断文章是否被点赞
     * @param postId
     * @param userId
     * @return
     */
    Long isPostLiked(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 判断文章是否被收藏
     * @param postId
     * @param userId
     * @return
     */
    Comparable<Long> isPostCollected(Long postId, Long userId);

    /**
     * 获取文章浏览量top10
     * @param postType
     * @return
     */
    List<PostWithBrowseCountVo> getBrowseTopPostList(Integer postType);

    /**
     * 更新文章内容
     * @param postContent
     */
    void updateContent(@Param("po") PostsContent postContent);

    /**
     * 获取其他用户文章列表
     * @param userId
     * @param loginUserId
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<UserPostVo> getOtherUserPostList(
            @Param("userId") Long userId,
            @Param("loginUserId") Long loginUserId,
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i);

    /**
     * 获取所有文章列表
     * @return
     */
    IPage<ESPost> selectAllPostWithContent(Page<ESPost> page);

    /**
     * 获取文章信息
     * @param id
     * @return
     */
    ESPost getESPostInfo(Long id);

    /**
     * 添加文章检测记录
     * @param type
     * @param checkReport
     */
    void insertCheckReport(@Param("type") String type, @Param("postId") Long postId, @Param("passStatus") Integer passStatus, @Param("checkReport") String checkReport);
}
