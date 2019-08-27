package com.autohome.dbtree.config;

import com.autohome.dbtree.service.impl.ConnManager;
import com.autohome.dbtree.service.impl.LockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

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
}
