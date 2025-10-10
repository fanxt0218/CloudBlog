package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.common.pojo.Vo.UserLevelInfoVo;
import com.cloudblog.common.result.AjaxResult;

public interface LevelMapper extends BaseMapper<Level> {

    /**
     * 获取用户等级信息
     * @param userId
     * @return
     */
    UserLevelInfoVo getUserLevelInfo(Long userId);
}
