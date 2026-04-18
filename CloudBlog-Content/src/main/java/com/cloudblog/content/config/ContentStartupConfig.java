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
@Order(1)
public class ContentStartupConfig  implements ApplicationRunner {

    // 等级列表
    public final TreeMap<Integer, Integer> Level_MAP = new TreeMap<>();

    // 话题信息
    public List<PublishPageTopicListVo> Topic_List = new ArrayList<>();

    // 资源分类信息
    public HashMap<String, List<String>> Resource_Category_List = new HashMap<>();

    private final LevelService levelService;

    private final ShareService shareService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CONTENT_LEVEL_KEY = "content:level";
    private static final String CONTENT_TOPIC_KEY = "content:topic";
    private static final String CONTENT_RESOURCE_CATEGORY_KEY = "content:resource:category";
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
        log.info("加载资源分类信息");
        initResourceCategoryList();
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
        Object levelMapObj = redisTemplate.opsForValue().get(CONTENT_LEVEL_KEY + ":map");
        if (levelMapObj != null) {
            if (levelMapObj instanceof TreeMap) {
                TreeMap<?, ?> rawMap = (TreeMap<?, ?>) levelMapObj;
                // 进行类型转换
                TreeMap<Integer, Integer> convertedMap = new TreeMap<>();
                rawMap.forEach((key, value) -> {
                    Integer intKey = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                    Integer intValue = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                    convertedMap.put(intKey, intValue);
                });
                return convertedMap;
            } else if (levelMapObj instanceof Map) {
                // 如果是普通 Map
                Map<?, ?> rawMap = (Map<?, ?>) levelMapObj;
                TreeMap<Integer, Integer> convertedMap = new TreeMap<>();
                rawMap.forEach((key, value) -> {
                    Integer intKey = key instanceof Number ? ((Number) key).intValue() : Integer.parseInt(key.toString());
                    Integer intValue = value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
                    convertedMap.put(intKey, intValue);
                });
                return convertedMap;
            }
        }
        // 如果 Redis 缓存为空，返回内存中的 Level_MAP
        return Level_MAP;
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

    /**
     * 初始化资源分类列表
     */
    public void initResourceCategoryList() {
        try {
            HashMap<String, List<String>> categories = new HashMap<>();
            categories.put("前端", Arrays.asList("图像识别","图像处理","编解码","直播技术"));
            categories.put("后端", Arrays.asList("Java","C++","C","C#","Python","Netty","PHP","Docker","Kotlin"));
            categories.put("行业研究", Arrays.asList("数据集","行业报告"));
            categories.put("移动开发", Arrays.asList("Android","HTML5","IOS","小程序"));
            categories.put("操作系统", Arrays.asList("Linux","桌面系统","Windows Server","MacOS","OS","Ubuntu","Unix","RedHat","CentOS"));
            categories.put("人工智能", Arrays.asList("机器学习","深度学习","搜索引擎","自然语言处理"));
            categories.put("物联网", Arrays.asList("公共安全","智慧城市","智慧交通","智能家居"));
            categories.put("信息化管理", Arrays.asList("管理软件","IT管理","项目管理","企业管理"));
            categories.put("网络技术", Arrays.asList("网络基础","网络设备","网络软件","网络监控"));
            categories.put("安全技术", Arrays.asList("网络安全","系统安全"));
            categories.put("数据库", Arrays.asList("MySQL","Oracle","SQLServer","SQLite","PostgreSQL","DB2","Redis"));
            categories.put("硬件开发", Arrays.asList("单片机","嵌入书","VB"));
            categories.put("游戏开发", Arrays.asList("Unity3D","cocos2D"));
            categories.put("考试认证", Arrays.asList("华为认证","软考","微软认证","思科认证"));
            categories.put("音视频", Arrays.asList("图像识别","图像处理"));
            categories.put("大数据", Arrays.asList("Hadoop","spark","Hive"));
            categories.put("存储", Arrays.asList("Microsoft","HP","IBM","EMC"));
            categories.put("云计算", Arrays.asList("平台管理","kubernetes","微服务"));
            categories.put("区块链", Arrays.asList("比特币","以太坊","Dapp"));
            categories.put("跨平台", Arrays.asList("ReactNative","CrossApp","APICloud"));
            categories.put("半导体", Arrays.asList("集成电路"));
            categories.put("其他", Arrays.asList("其他"));

            redisTemplate.opsForValue().set(CONTENT_RESOURCE_CATEGORY_KEY, categories, Duration.ofHours(DEFAULT_TTL_HOURS));
        } catch (Exception e) {
            log.error("加载资源分类信息失败：{}", e.getMessage(), e);
        }
    }

    public HashMap<String, List<String>> getResourceCategoryList() {
        Object resourceCategoryList = redisTemplate.opsForValue().get(CONTENT_RESOURCE_CATEGORY_KEY);
        if (resourceCategoryList != null) {
            return (HashMap<String, List<String>>) resourceCategoryList;
        } else {
            initResourceCategoryList();
            return (HashMap<String, List<String>>) redisTemplate.opsForValue().get(CONTENT_RESOURCE_CATEGORY_KEY);
        }
    }
}
