package com.report.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置 - 配置静态资源访问
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片上传目录的访问
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:/home/admin/.openclaw/workspace/REPORT/upload/");
    }
}
