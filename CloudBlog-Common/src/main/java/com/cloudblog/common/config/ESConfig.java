package com.cloudblog.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ESConfig {

    @Value("${elasticsearch.server.host}")
    private String host;

    @Value("${elasticsearch.server.port}")
    private Integer port;

    @Value("${elasticsearch.server.protocol}")
    private String protocol;

    @Bean
    public RestHighLevelClient client() {
        log.info("初始化ES");
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, protocol)
                )
        );
    }
}
