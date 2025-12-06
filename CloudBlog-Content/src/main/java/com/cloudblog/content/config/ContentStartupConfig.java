package com.cloudblog.content.config;

import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.content.service.LevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Component
@Order(2)
public class ContentStartupConfig  implements ApplicationRunner {

    // 等级列表
    public static final TreeMap<Integer, Integer> Level_MAP = new TreeMap<>();

    private final LevelService levelService;

    public ContentStartupConfig(LevelService levelService) {
        this.levelService = levelService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("加载等级信息");
        initLevelMap();
    }

    /**
     * 初始化等级表
     */
    private void initLevelMap() {
        List<Level> levelList = levelService.getLevelList();
        levelList.forEach(level -> {
            Level_MAP.put(level.getLevel(), level.getExpThreshold());
        });
    }
}
