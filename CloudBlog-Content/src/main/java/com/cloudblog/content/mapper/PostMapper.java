package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cloudblog.common.pojo.DoMain.Posts;
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
    IPage<UserLikeListVo> getUserLikeList(IPage<UserLikeListVo> page, @Param("userId") Long userId, @Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime, @Param("type") int ordinal);
}
