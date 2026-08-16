package com.neonnexus.vcdp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类
 * 扫描 Mapper 接口
 */
@Configuration
@MapperScan("com.neonnexus.vcdp.mapper")
public class MyBatisConfig {
    // MyBatis Spring Boot Starter 会自动配置，这里只需要扫描 Mapper 接口
}

