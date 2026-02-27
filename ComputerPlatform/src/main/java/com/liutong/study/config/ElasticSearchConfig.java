package com.liutong.study.config;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    /**
     * 配置底层客户端拦截器，解决版本兼容性问题
     */
    @Bean
    public RestClientBuilderCustomizer restClientBuilderCustomizer() {
        return builder -> builder.setHttpClientConfigCallback(httpClientBuilder -> {

            // 1. 请求拦截器 (Request Interceptor)
            // 作用：解决 "406 Not Acceptable" 错误
            // 原理：强制将 Content-Type 设置为普通 JSON，防止客户端发送 "compatible-with=8" 的头
            httpClientBuilder.addInterceptorLast((HttpRequestInterceptor) (request, context) -> {
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Accept", "application/json");
            });

            // 2. 响应拦截器 (Response Interceptor)
            // 作用：解决 "Missing [X-Elastic-Product] header" 错误
            // 原理：Spring Boot 客户端会检查服务器返回的头里有没有这个标签，OpenSearch 没有，我们人工贴上去欺骗客户端
            httpClientBuilder.addInterceptorLast((HttpResponseInterceptor) (response, context) -> {
                response.addHeader("X-Elastic-Product", "Elasticsearch");
            });

            return httpClientBuilder;
        });
    }
}