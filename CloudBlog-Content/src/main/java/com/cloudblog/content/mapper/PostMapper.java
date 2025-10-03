package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudblog.common.pojo.DoMain.Posts;
import com.cloudblog.common.pojo.Vo.UserCollectListVo;
import com.cloudblog.common.pojo.Vo.UserLikeListVo;
import jakarta.annotation.security.PermitAll;
import org.apache.ibatis.annotations.Param;

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
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime,
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
}
