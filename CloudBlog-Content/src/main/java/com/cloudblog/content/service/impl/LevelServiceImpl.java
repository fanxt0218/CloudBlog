package com.cloudblog.content.service.impl;

import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.content.mapper.LevelMapper;
import com.cloudblog.content.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelServiceImpl implements LevelService {

    @Autowired
    private LevelMapper levelMapper;

    public List<Level> getLevelList() {
        return levelMapper.selectList(null);
    }
}
