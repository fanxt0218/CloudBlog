package com.cloudblog.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class RedisTokenBlacklistUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 将 token 加入黑名单
     * @param token 被拉黑的 token
     * @param ttl 剩余有效期（token 原本的剩余时间）
     */
    public void addToBlacklist(String token, Duration ttl) {
        if (token == null || token.isEmpty()) {
            return;
        }
        
        // 去除 Bearer 前缀
        String cleanToken = token.trim();
        if (cleanToken.toLowerCase().startsWith("bearer ")) {
            cleanToken = cleanToken.substring(7).trim();
        }
        
        String key = BLACKLIST_PREFIX + cleanToken;
        redisTemplate.opsForValue().set(key, "blacklisted", ttl);
        log.info("Token 已加入黑名单：{}, 过期时间：{}", key, ttl);
    }

    /**
     * 检查 token 是否在黑名单中
     * @param token 要检查的 token
     * @return true-在黑名单中，false-不在黑名单中
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // 去除 Bearer 前缀
        String cleanToken = token.trim();
        if (cleanToken.toLowerCase().startsWith("bearer ")) {
            cleanToken = cleanToken.substring(7).trim();
        }
        
        String key = BLACKLIST_PREFIX + cleanToken;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
}
