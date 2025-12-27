package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Comments;
import org.apache.ibatis.annotations.Param;

public interface CommentMapper extends BaseMapper<Comments> {

    /**
     * 获取用户评论数量
     * @param userId
     * @return
     */
    Integer getUserCommentCount(Long userId);

    /**
     * 计算评论数量
     * @param contentId
     * @param type
     * @return
     */
    Long calculateCommentCount(@Param("contentId") Long contentId, @Param("type") Integer type);
}
