package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Comments;

public interface CommentMapper extends BaseMapper<Comments> {

    /**
     * 获取用户评论数量
     * @param userId
     * @return
     */
    Integer getUserCommentCount(Long userId);
}
