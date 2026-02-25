package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.common.pojo.Vo.UserLevelInfoVo;
import com.cloudblog.common.result.AjaxResult;

import java.util.List;

public interface LevelService {

    /**
     * 获取等级列表
     */
    List<Level> getLevelList();

    /**
     * 获取用户等级信息
     * @param userId
     * @return
     */
    AjaxResult getUserLevelInfo(Long userId);

    /**
     * 添加经验值
     * @param userId
     * @param exp
     */
    void addExp(Long userId, int exp);
}
