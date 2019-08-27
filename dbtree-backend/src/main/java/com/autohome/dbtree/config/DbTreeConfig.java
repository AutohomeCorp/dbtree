package com.autohome.dbtree.config;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import com.autohome.dbtree.contract.DbInfo;
import com.autohome.dbtree.contract.DbServer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class DbTreeConfig {

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public Map<String, DbInfo> dbInfoMap() throws IOException {
        String json = readJsonFile("dbconfig/db-config.json");
        Map<String, DbInfo> dbInfoMap = objectMapper.readValue(json, new TypeReference<Map<String, DbInfo>>() {});
        return dbInfoMap;
    }

    @Bean
    public Map<String, DbServer> dbServerMap() throws IOException {
        String json = readJsonFile("dbconfig/db-server.json");
        Map<String, DbServer> dbServerMap = objectMapper.readValue(json, new TypeReference<Map<String, DbServer>>() {});
        return dbServerMap;
    }

    private String readJsonFile(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        try (StringWriter stringWriter = new StringWriter()) {
            IOUtils.copy(classPathResource.getInputStream(), stringWriter, StandardCharsets.UTF_8);
            return stringWriter.toString();
        }
    }
}
