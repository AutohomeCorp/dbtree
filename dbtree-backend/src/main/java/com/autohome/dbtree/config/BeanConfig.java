package com.autohome.dbtree.config;

import com.autohome.dbtree.service.impl.ConnManager;
import com.autohome.dbtree.service.impl.LockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Value("${mybatis.base.folder}")
    private String mybatisBaseFolder;

    @Value("${mybatis.mysql.connector}")
    private String mysqlConnector;

    @Value("${mybatis.sqlserver.connector}")
    private String sqlserverConnector;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(destroyMethod = "close")
    public ConnManager connManager() {
        return new ConnManager();
    }

    @Bean(destroyMethod = "unLock")
    public LockService lockService() {
        return new LockService();
    }

    @Bean
    public MybatisCodeGeneratorConfig mybatisCodeGeneratorConfig() {
        MybatisCodeGeneratorConfig mybatisCodeGeneratorConfig = new MybatisCodeGeneratorConfig();
        mybatisCodeGeneratorConfig.setMybatisBaseFolder(mybatisBaseFolder);
        mybatisCodeGeneratorConfig.setMysqlConnector(mysqlConnector);
        mybatisCodeGeneratorConfig.setSqlserverConnector(sqlserverConnector);
        return mybatisCodeGeneratorConfig;
    }
}
