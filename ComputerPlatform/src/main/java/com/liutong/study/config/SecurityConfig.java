package com.liutong.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF (防止跨站请求伪造拦截 POST 请求)
                .csrf(csrf -> csrf.disable())
                // 2. 开启跨域支持 (允许前端 Vue 访问后端)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 3. 权限控制：暂时允许所有请求访问 (开发阶段方便测试)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // 配置跨域 (CORS)，允许前端 5173 端口访问
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 允许所有来源
        config.addAllowedHeader("*");        // 允许所有请求头
        config.addAllowedMethod("*");        // 允许所有方法 (GET, POST等)
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}