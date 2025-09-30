package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Tag;

import java.util.List;

public interface InterestMapper extends BaseMapper<Tag> {

    /**
     * 获取用户兴趣
     * @param userId
     * @return
     */
    List<Tag> getUserInterest(Long userId);
}
