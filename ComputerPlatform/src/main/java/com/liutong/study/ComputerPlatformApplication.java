package com.liutong.study;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.liutong.study.mapper")
@EnableScheduling
public class ComputerPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComputerPlatformApplication.class, args);
    }
}