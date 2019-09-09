package com.autohome.dbtree.config;

import org.springframework.beans.factory.annotation.Value;

public class MybatisCodeGeneratorConfig {

    private String mybatisBaseFolder;

    private String mysqlConnector;

    private String sqlserverConnector;

    public String getMybatisBaseFolder() {
        return mybatisBaseFolder;
    }

    public void setMybatisBaseFolder(String mybatisBaseFolder) {
        this.mybatisBaseFolder = mybatisBaseFolder;
    }

    public String getMysqlConnector() {
        return mysqlConnector;
    }

    public void setMysqlConnector(String mysqlConnector) {
        this.mysqlConnector = mysqlConnector;
    }

    public String getSqlserverConnector() {
        return sqlserverConnector;
    }

    public void setSqlserverConnector(String sqlserverConnector) {
        this.sqlserverConnector = sqlserverConnector;
    }
}
