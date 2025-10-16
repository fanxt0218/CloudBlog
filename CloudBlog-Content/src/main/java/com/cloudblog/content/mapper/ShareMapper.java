package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Share;
import com.cloudblog.common.pojo.Vo.UserShareVo;
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
    void addShareBrowseCount(Long postId, Long userId);
}
