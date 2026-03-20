package com.cloudblog.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 根据key获取缓存数据
     */
    public Object get(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o;
    }

    /**
     * 根据 key 获取缓存数据并转换为指定类型
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        try {
            String json = objectMapper.writeValueAsString(value);
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("转换 Redis 数据失败", e);
            return null;
        }
    }


    /**
     * 根据 key 获取缓存数据的 JSON 字符串
     */
    public String getAsString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("转换 Redis 数据为字符串失败", e);
            return null;
        }
    }

    /**
     * 清除缓存，有key
     */
    public void clear(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 清除所有缓存
     */
    public void clearAll() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushDb();
    }
}
