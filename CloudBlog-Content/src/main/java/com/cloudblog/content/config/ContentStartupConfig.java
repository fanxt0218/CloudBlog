package com.cloudblog.content.config;

import com.cloudblog.common.pojo.DoMain.Level;
import com.cloudblog.common.pojo.DoMain.Share;
import com.cloudblog.common.pojo.Vo.PublishPageTopicListVo;
import com.cloudblog.content.service.LevelService;
import com.cloudblog.content.service.ShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@Order(2)
public class ContentStartupConfig  implements ApplicationRunner {

    // 等级列表
    public final TreeMap<Integer, Integer> Level_MAP = new TreeMap<>();

    // 话题信息
    public List<PublishPageTopicListVo> Topic_List = new ArrayList<>();

    private final LevelService levelService;

    private final ShareService shareService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CONTENT_LEVEL_KEY = "content:level";
    private static final String CONTENT_TOPIC_KEY = "content:topic";
    private static final long DEFAULT_TTL_HOURS = 24;

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
    public void initLevelMap() {
        List<Level> levelList = levelService.getLevelList();
        levelList.forEach(level -> {
            Level_MAP.put(level.getLevel(), level.getExpThreshold());
        });

        String key = CONTENT_LEVEL_KEY + ":map";
        redisTemplate.opsForValue().set(key, Level_MAP);

        log.info("批量保存等级信息到 Redis，共{}条记录", Level_MAP.size());
    }

    public TreeMap<Integer, Integer> getLevelMap() {
        // 从缓存中查询整个等级列表
        Object levelMap = redisTemplate.opsForValue().get(CONTENT_LEVEL_KEY + ":list");
        if (levelMap != null) {
            return (TreeMap<Integer, Integer>) levelMap;
        }
        return null;
    }

    /**
     * 初始化话题列表
     */
    public void initTopicList() {
        try {
            List<PublishPageTopicListVo> topicList = (List<PublishPageTopicListVo>) shareService.getPublishPageTopicList().get("data");

            Topic_List.addAll(topicList);

            for (int i = 0; i < topicList.size(); i++) {
                PublishPageTopicListVo topic = topicList.get(i);
                String key = CONTENT_TOPIC_KEY + ":" + topic.getId();
                redisTemplate.opsForValue().set(key, topic, Duration.ofHours(DEFAULT_TTL_HOURS));
            }

            redisTemplate.opsForValue().set(CONTENT_TOPIC_KEY + ":list", topicList, Duration.ofHours(DEFAULT_TTL_HOURS));

            log.info("批量保存话题信息到 Redis，共{}条记录", topicList.size());
        } catch (Exception e) {
            log.error("加载话题信息失败：{}", e.getMessage(), e);
        }
    }

    public PublishPageTopicListVo getTopicById(Integer topicId) {
        String key = CONTENT_TOPIC_KEY + ":" + topicId;
        return (PublishPageTopicListVo) redisTemplate.opsForValue().get(key);
    }

    public List<PublishPageTopicListVo> getTopicList() {
        Object topicList = redisTemplate.opsForValue().get(CONTENT_TOPIC_KEY + ":list");
        if (topicList != null) {
            return (List<PublishPageTopicListVo>) topicList;
        }
        return null;
    }
}
