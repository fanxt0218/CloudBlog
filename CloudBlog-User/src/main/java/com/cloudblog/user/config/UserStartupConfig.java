package com.cloudblog.user.config;

import com.cloudblog.common.pojo.Dto.PageResponse;
import com.cloudblog.common.pojo.Vo.IndexUserListVo;
import com.cloudblog.common.result.AjaxResult;
import com.cloudblog.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(1)
public class UserStartupConfig implements ApplicationRunner {

    public final HashMap<Long, Long> USER_RANKING_MAP = new HashMap<>();

    private final UserInfoService userInfoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_RANKING_KEY = "user:ranking";
    private static final long DEFAULT_TTL_HOURS = 24;

    public UserStartupConfig(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("加载用户排名信息");
        loadUserRanking();
    }

    public void loadUserRanking() {
        Long usersCount = userInfoService.getUsersCount();
        AjaxResult indexUserList = userInfoService.getIndexUserList(null, Math.toIntExact(usersCount), null);
        PageResponse<IndexUserListVo> data = (PageResponse<IndexUserListVo>) indexUserList.get("data");
        data.getContent().forEach(user -> {
            USER_RANKING_MAP.put(user.getUserId(), user.getRankNum());
        });

        Map<String, Long> serializedMap = new HashMap<>();
        USER_RANKING_MAP.forEach((userId, rank) -> {
            String key = USER_RANKING_KEY + ":" + userId;
            serializedMap.put(key, rank);
        });

        redisTemplate.opsForValue().multiSet(serializedMap);

        for (String key : serializedMap.keySet()) {
            redisTemplate.expire(key, Duration.ofHours(DEFAULT_TTL_HOURS));
        }

        log.info("批量保存用户排名到 Redis，共{}条记录", USER_RANKING_MAP.size());
    }

    public Long getUserRanking(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = USER_RANKING_KEY + ":" + userId;
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }

        return null;
    }
}
