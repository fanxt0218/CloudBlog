package com.cloudblog.common.config;

import com.cloudblog.common.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.*;

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
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 修改已有的 StringConverter
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter stringConverter) {
                stringConverter.setDefaultCharset(StandardCharsets.UTF_8);
                stringConverter.setWriteAcceptCharset(false);
            }

            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                jacksonConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new UserInterceptor());
//                .addPathPatterns("/**")
//                .excludePathPatterns("/user/login", "/user/register", "/user/logout");
    }
}
