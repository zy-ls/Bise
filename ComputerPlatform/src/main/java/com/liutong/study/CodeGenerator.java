package com.liutong.study;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.sql.Types;
import java.util.Collections;

/**
 * 代码生成器：一键生成 Entity, Mapper, Service, Controller
 */
public class CodeGenerator {

    public static void main(String[] args) {
        // 1. 数据库配置 (请修改为你的 MySQL 密码)
        String url = "jdbc:mysql://localhost:3306/study-platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "Goodluck0808."; // TODO: 修改成你的数据库密码

        // 2. 开始生成
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("LiuTong") // 设置作者
                            .enableSwagger() // 开启 Swagger 模式 (可选)
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.liutong.study") // 设置父包名
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置 Mapper XML 输出路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("sys_user", "note_info", "note_category", "note_version", "sys_resource", "audit_log", "comment", "chat_message") // 设置需要生成的表名
                            .addTablePrefix("sys_", "note_") // 设置过滤表前缀 (生成的类名不会带 sys_ 或 note_)

                            // Entity 策略
                            .entityBuilder()
                            .enableLombok() // 开启 Lombok
                            .enableTableFieldAnnotation() // 开启字段注解
                            .logicDeleteColumnName("deleted") // 逻辑删除字段

                            // Controller 策略
                            .controllerBuilder()
                            .enableRestStyle(); // 开启 @RestController
                })
                .templateEngine(new VelocityTemplateEngine()) // 使用 Velocity 引擎
                .execute();

        System.out.println("====== 代码生成完成！======");
    }
}