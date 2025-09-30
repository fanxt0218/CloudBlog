package com.cloudblog.content.service;

import com.cloudblog.common.pojo.DoMain.Level;

import java.util.List;

public interface LevelService {

    /**
     * 获取等级列表
     */
    List<Level> getLevelList();
}
