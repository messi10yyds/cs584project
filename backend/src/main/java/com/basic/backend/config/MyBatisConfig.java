package com.basic.backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.basic.backend.mapper")
public class MyBatisConfig {
    // XML 路径、驼峰映射等放到 application.yml 里配置即可
}