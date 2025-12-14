package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Share;
import com.cloudblog.common.pojo.Vo.IndexFocusArticleVo;
import com.cloudblog.common.pojo.Vo.IndexTopicVo;
import com.cloudblog.common.pojo.Vo.UserShareVo;
import com.cloudblog.common.result.AjaxResult;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShareMapper extends BaseMapper<Share> {

    /**
     * 获取用户动态列表
     * @param userId
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<UserShareVo> getUserPostList(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i);

    /**
     * 增加动态浏览次数
     * @param postId
     * @param userId
     */
    void addShareBrowseCount(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 获取首页动态列表
     * @param lastId
     * @param lastCreateTime
     * @param i
     * @return
     */
    List<UserShareVo> getIndexPostList(
            @Param("lastId") Long lastId,
            @Param("lastCreateTime") LocalDateTime lastCreateTime,
            @Param("size") int i,
            @Param("topicId") Integer topicId
    );

    List<IndexTopicVo> getTopicList();

    /**
     * 获取关注动态列表
     * @param lastTargetId
     * @param lastCreateTime
     * @param i
     * @param userId
     * @return
     */
    List<IndexFocusArticleVo> getFocusShareList(@Param("lastTargetId") Long lastTargetId, @Param("lastCreateTime") LocalDateTime lastCreateTime, @Param("size") int i, @Param("userId") Long userId);
}
