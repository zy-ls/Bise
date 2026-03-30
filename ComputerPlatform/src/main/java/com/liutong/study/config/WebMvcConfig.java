package com.liutong.study.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 🚨 核心配置：只拦截以 /admin/ 开头的后台绝密接口
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**", "/report/list", "/report/process");
    }
}