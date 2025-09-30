package com.cloudblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Collect;

public interface CollectMapper extends BaseMapper<Collect> {

    Integer getUserCollectCount(Long userId);
}
