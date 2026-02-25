package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Likes;
import com.cloudblog.common.pojo.DoMain.UserInterest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LikeMapper extends BaseMapper<Likes> {

    /**
     * 点赞/取消点赞
     * @param userId
     * @param targetId
     * @param status
     */
    void liking(
            @Param("userId") Long userId,
            @Param("targetId") Long targetId,
            @Param("status") Integer status,
            @Param("type") Integer type
            );

    /**
     * 获取用户点赞数量
     * @param userId
     * @return
     */
    Integer getUserLikeCount(Long userId);

    /**
     * 计算点赞数量
     * @param contentId
     * @param type
     * @return
     */
    Long calculateLikeCount(@Param("contentId") Long contentId, @Param("type") Integer type);

    /**
     * 获取用户兴趣
     * @param userId
     * @return
     */
    List<UserInterest> getUserInterests(Long userId);
}
