package com.cloudblog.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.resource.path}")
    private String resourcePath;

    @Value("${file.resource.prefix}")
    private String resourcePrefix;

    @Autowired
    private ThreadPoolTaskExecutor mvcTaskExecutor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // addResourceHandler 参数是要配置的URL访问前缀，/** 表示所有子路径
        // addResourceLocations 参数是资源的物理路径，必须以 file: 开头，且结尾必须有 /
        registry.addResourceHandler(resourcePrefix + "/**")
                .addResourceLocations("file:" + resourcePath + "/");
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置自定义的异步任务执行器
        configurer.setTaskExecutor(mvcTaskExecutor);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 配置String类型的字符编码为UTF-8
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        converters.add(stringConverter);

        // 确保Jackson也使用UTF-8
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(jacksonConverter);
    }
}
