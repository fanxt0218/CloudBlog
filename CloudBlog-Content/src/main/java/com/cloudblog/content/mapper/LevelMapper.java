package com.cloudblog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.common.pojo.Vo.UserLevelInfoVo;
import com.cloudblog.common.result.AjaxResult;
import org.apache.ibatis.annotations.Param;

public interface LevelMapper extends BaseMapper<Level> {

    /**
     * 获取用户等级信息
     * @param userId
     * @return
     */
    UserLevelInfoVo getUserLevelInfo(Long userId);

    /**
     * 添加经验值
     * @param userId
     * @param exp
     */
    void addExp(@Param("userId") Long userId, @Param("exp") int exp);
}
