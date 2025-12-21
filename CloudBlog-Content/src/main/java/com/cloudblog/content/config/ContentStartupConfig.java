package com.cloudblog.content.config;

import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.common.pojo.DoMain.Share;
import com.cloudblog.common.pojo.Vo.PublishPageTopicListVo;
import com.cloudblog.content.service.LevelService;
import com.cloudblog.content.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Component
@Order(2)
public class ContentStartupConfig  implements ApplicationRunner {

    // 等级列表
    public static final TreeMap<Integer, Integer> Level_MAP = new TreeMap<>();

    // 话题信息
    public static List<PublishPageTopicListVo> Topic_List = new ArrayList<>();

    private final LevelService levelService;

    private final ShareService shareService;

    public ContentStartupConfig(LevelService levelService, ShareService shareService) {
        this.levelService = levelService;
        this.shareService = shareService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("加载等级信息");
        initLevelMap();
        log.info("加载话题信息");
        initTopicList();
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

    /**
     * 初始化话题列表
     */
    private void initTopicList() {
        shareService.getPublishPageTopicList();
    }
}
