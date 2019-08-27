package com.autohome.dbtree.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.alibaba.druid.pool.DruidDataSource;
import com.autohome.dbtree.contract.DbInfo;
import com.autohome.dbtree.contract.DbServer;

public class ConnManager {

    private ConcurrentHashMap<String, DruidDataSource> dataSourceMap = new ConcurrentHashMap<>();

    @Resource
    private Map<String, DbInfo> dbInfoMap;

    @Resource
    private Map<String, DbServer> dbServerMap;

    public Connection fetchConnection(String dbName) throws SQLException {
        if (dataSourceMap.containsKey(dbName)) {
            return dataSourceMap.get(dbName).getConnection();
        }
        if (!dbInfoMap.containsKey(dbName)) {
            throw new RuntimeException("unknown db: " + dbName);
        }

        initDataSource(dbName);

        return dataSourceMap.get(dbName).getConnection();
    }

    public void close() {
        for (Map.Entry<String, DruidDataSource> entry : dataSourceMap.entrySet()) {
            entry.getValue().close();
        }
    }

    private synchronized void initDataSource(String dbName) {
        DbInfo dbInfo = dbInfoMap.get(dbName);
        DbServer dbServer = dbServerMap.get(dbInfo.getDb_server());
        DruidDataSource druidDataSource = new DruidDataSource();
        String dataSourceUrl = null;
        if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
            druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSourceUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&verifyServerCertificate=false", dbServer.getHost(), dbServer.getPort(), dbInfo.getDb_name());
        } else if(dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
            druidDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            dataSourceUrl = String.format("jdbc:sqlserver://%s:%s;database=%s", dbServer.getHost(), dbServer.getPort(), dbName);
        } else if(dbServer.getDb_type().equalsIgnoreCase("oracle")) {
            druidDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        } else {
            throw new IllegalArgumentException("unknown db_type: " + dbServer.getDb_type());
        }

        druidDataSource.setUrl(dataSourceUrl);
        druidDataSource.setUsername(dbServer.getUser());
        druidDataSource.setPassword(dbServer.getPassword());
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(0);
        druidDataSource.setMaxActive(2);
        druidDataSource.setMaxWait(600000);
        druidDataSource.setValidationQuery("SELECT 1");

        dataSourceMap.put(dbName, druidDataSource);
    }
}
