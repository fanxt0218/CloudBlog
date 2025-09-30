package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Likes;

public interface LikeMapper extends BaseMapper<Likes> {

    Integer getUserLikeCount(Long userId);
}
