package com.liutong.study;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.liutong.study.mapper") // 这个千万别丢
public class ComputerPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComputerPlatformApplication.class, args);
    }
}