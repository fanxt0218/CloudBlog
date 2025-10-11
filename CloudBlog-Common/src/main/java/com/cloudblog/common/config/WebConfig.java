package com.cloudblog.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.resource.path}")
    private String resourcePath;

    @Value("${file.resource.prefix}")
    private String resourcePrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // addResourceHandler 参数是要配置的URL访问前缀，/** 表示所有子路径
        // addResourceLocations 参数是资源的物理路径，必须以 file: 开头，且结尾必须有 /
        registry.addResourceHandler(resourcePrefix + "/**")
                .addResourceLocations("file:" + resourcePath + "/");
    }
}
