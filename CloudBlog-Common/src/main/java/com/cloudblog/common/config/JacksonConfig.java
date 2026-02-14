package com.cloudblog.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        // 忽略null值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 禁用时间戳格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 设置默认字符编码
        mapper.getFactory().setCharacterEscapes(null);
        return mapper;
    }
}