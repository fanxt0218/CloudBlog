package com.cloudblog.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean("exportTaskExecutor")
    public ThreadPoolTaskExecutor exportTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数，根据你的服务器CPU核心数调整（通常建议为核心数*2）
        executor.setCorePoolSize(4);
        // 最大线程数，不宜过高，需考虑数据库连接池大小
        executor.setMaxPoolSize(8);
        // 队列容量，用于缓冲
        executor.setQueueCapacity(100);
        // 线程空闲存活时间
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("export-thread-");
        // 拒绝策略：由调用者线程执行该任务，避免任务丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
