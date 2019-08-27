package com.autohome.dbtree.service;

import com.autohome.dbtree.contract.ColumnInfo;
import com.autohome.dbtree.contract.TableInfo;

import java.util.List;

public interface IDbService {

    List<TableInfo> findTablesByDb(String dbName);

    List<TableInfo> findTablesByName(String dbName, List<String> tableList);

    List<ColumnInfo> findColumnsByTable(String dbName, String tableName);

    Boolean updateColumnComment(String dbName, String tableName, String column, String comment);

    Boolean updateTableComment(String dbName, String tableName, String newComment);
}
