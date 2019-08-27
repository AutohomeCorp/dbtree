package com.autohome.dbtree.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.autohome.dbtree.contract.ColumnInfo;
import com.autohome.dbtree.contract.DbInfo;
import com.autohome.dbtree.contract.DelegateRule;
import com.autohome.dbtree.contract.TableInfo;
import com.autohome.dbtree.service.IDbService;
import com.autohome.dbtree.util.JdbcUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MysqlDbService implements IDbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDbService.class);

    @Resource
    private ConnManager connManager;

    @Resource
    private Map<String, DbInfo> dbInfoMap;

    @Override
    public List<TableInfo> findTablesByDb(String dbName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT TABLE_NAME, TABLE_COMMENT ")
                .append("FROM information_schema.tables ")
                .append("WHERE TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA = ?");
        DbInfo dbInfo = dbInfoMap.get(dbName);
        if (dbInfo.getSplit_table_rules() != null && dbInfo.getSplit_table_rules().size() > 0) {
            for (DelegateRule delegateRule : dbInfo.getSplit_table_rules()) {
                sqlBuilder.append(String.format(" AND (TABLE_NAME NOT LIKE '%s' OR TABLE_NAME = '%s')", delegateRule.getTable_pattern(), delegateRule.getDelegate_table()));
            }
        }
        sqlBuilder.append(" ORDER BY TABLE_NAME");
        try {
            Connection connection = connManager.fetchConnection(dbName);
            return JdbcUtil.executeQuery(connection, dbName, sqlBuilder.toString(), MysqlDbService::resultSetToTableInfo,
                    preparedStatement -> {
                        try {
                            preparedStatement.setString(1, dbName);
                        } catch (Exception ex) {
                            LOGGER.error("error when prepareFindTablesByDbStatement", ex);
                        }
                    });
        } catch (Exception ex) {
            throw new RuntimeException("error when readTableFromDb, dbName: " + dbName, ex);
        }
    }

    @Override
    public List<TableInfo> findTablesByName(String dbName, List<String> tableList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT TABLE_NAME, TABLE_COMMENT ")
                .append("FROM information_schema.tables ")
                .append("WHERE TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA = ? AND TABLE_NAME IN ");
        JdbcUtil.appendInClause(sqlBuilder, tableList.size());
        sqlBuilder.append(" ORDER BY TABLE_NAME");
        try {
            Connection connection = connManager.fetchConnection(dbName);

            return JdbcUtil.executeQuery(connection, dbName, sqlBuilder.toString(), MysqlDbService::resultSetToTableInfo,
                    preparedStatement -> {
                        try {
                            preparedStatement.setString(1, dbName);
                            int beginIndex = 2;
                            for (String tableName : tableList) {
                                preparedStatement.setString(beginIndex, tableName);
                                beginIndex++;
                            }
                        } catch (Exception ex) {
                            LOGGER.error("error when prepareFindTablesByNameStatement", ex);
                        }
                    });
        } catch (Exception ex) {
            throw new RuntimeException("error when findTablesByName, dbName: " + dbName, ex);
        }
    }

    @Override
    public List<ColumnInfo> findColumnsByTable(String dbName, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, COLUMN_DEFAULT, ")
                .append("IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, ")
                .append("NUMERIC_SCALE, COLUMN_TYPE, COLUMN_KEY, EXTRA, COLUMN_COMMENT")
                .append(" FROM information_schema.columns ")
                .append(" WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");

        try {
            Connection connection = connManager.fetchConnection(dbName);

            return JdbcUtil.executeQuery(connection, dbName, sqlBuilder.toString(), MysqlDbService::resultSetToColumnInfo, preparedStatement -> {
                try {
                    preparedStatement.setString(1, dbName);
                    preparedStatement.setString(2, tableName);
                } catch (Exception ex) {
                    LOGGER.error("", ex);
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException("error when readColumnFromDb, dbName: " + dbName, ex);
        }
    }

    @Override
    public Boolean updateColumnComment(String dbName, String tableName, String column, String comment) {
        List<ColumnInfo> columnInfoList = findColumnsByTable(dbName, tableName);
        ColumnInfo columnInfo = columnInfoList.stream()
                .filter(x -> x.getColumnName().equalsIgnoreCase(column)).findFirst().get();
        if (columnInfo.getColumnComment().equalsIgnoreCase(comment)) {
            return true;
        }
        StringBuilder updateCommentSql = new StringBuilder();
        updateCommentSql.append("ALTER TABLE ").append(tableName).append(" MODIFY COLUMN ").append(column)
                .append(" ").append(columnInfo.getColumnType());
        if (columnInfo.getNullable().equalsIgnoreCase("NO")) {
            updateCommentSql.append(" NOT NULL ");
        }
        if (columnInfo.getColumnDefault() != null) {
            String columnDefault = columnInfo.getColumnDefault();
            if (columnDefault.startsWith("(") && columnDefault.endsWith(")")) {
                //兼容mysql8表达式默认值
                updateCommentSql.append(" DEFAULT ").append(columnInfo.getColumnDefault());
            } else if (isNumericType(columnInfo.getDataType())) {
                updateCommentSql.append(" DEFAULT ").append(columnInfo.getColumnDefault());
            } else if ((columnInfo.getDataType().equalsIgnoreCase("datetime") ||
                    columnInfo.getDataType().equalsIgnoreCase("timestamp"))
                    && columnInfo.getColumnDefault().toUpperCase().contains("CURRENT_TIMESTAMP")) {
                updateCommentSql.append(" DEFAULT ").append(columnInfo.getColumnDefault());
            } else {
                updateCommentSql.append(" DEFAULT '").append(columnInfo.getColumnDefault()).append("' ");
            }
        }

        if (!Strings.isNullOrEmpty(columnInfo.getExtra())) {
            updateCommentSql.append(" ").append(columnInfo.getExtra());
        }
        updateCommentSql.append(" COMMENT ?");

        try {
            Connection connection = connManager.fetchConnection(dbName);
            return JdbcUtil.execute(connection, dbName, updateCommentSql.toString(), preparedStatement -> {
                try {
                    preparedStatement.setString(1, comment);
                } catch (Exception ex) {
                    LOGGER.error("", ex);
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException("error when updateColumnComment, dbName: " + dbName, ex);
        }
    }

    @Override
    public Boolean updateTableComment(String dbName, String tableName, String newComment) {
        String updateCommentSql = String.format("ALTER TABLE %s COMMENT '%s'", tableName, newComment);
        try {
            Connection connection = connManager.fetchConnection(dbName);
            return JdbcUtil.execute(connection, dbName, updateCommentSql, preparedStatement -> {
            });
        } catch (Exception ex) {
            throw new RuntimeException("error when updateTableComment, dbName: " + dbName, ex);
        }
    }

    private static List<TableInfo> resultSetToTableInfo(ResultSet resultSet) {
        List<TableInfo> tableInfoList = new ArrayList<>();

        try {
            while (resultSet.next()) {
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(resultSet.getString("TABLE_NAME"));
                tableInfo.setDescription(resultSet.getString("TABLE_COMMENT"));
                tableInfoList.add(tableInfo);
            }
        } catch (Exception ex) {
            LOGGER.error("error when resultSetToTableInfo", ex);
        }

        return tableInfoList;
    }

    private static List<ColumnInfo> resultSetToColumnInfo(ResultSet resultSet) {
        List<ColumnInfo> columnInfos = new ArrayList<>();

        try {
            while (resultSet.next()) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setColumnName(resultSet.getString("COLUMN_NAME"));
                columnInfo.setColumnType(resultSet.getString("COLUMN_TYPE"));
                columnInfo.setColumnComment(resultSet.getString("COLUMN_COMMENT"));
                columnInfo.setNullable(resultSet.getString("IS_NULLABLE"));
                columnInfo.setDataType(resultSet.getString("DATA_TYPE"));
                columnInfo.setColumnDefault(resultSet.getString("COLUMN_DEFAULT"));
                String columnKey = resultSet.getString("COLUMN_KEY");
                columnInfo.setPrecision(resultSet.getInt("NUMERIC_PRECISION"));
                columnInfo.setScale(resultSet.getInt("NUMERIC_SCALE"));
                if (columnKey != null && columnKey.equalsIgnoreCase("PRI")) {
                    columnInfo.setPrimaryKey(1);
                } else {
                    columnInfo.setPrimaryKey(0);
                }
                String extra = resultSet.getString("EXTRA");
                columnInfo.setExtra(extra);
                if (extra.contains("auto_increment")) {
                    columnInfo.setAutoIncrement(1);
                } else {
                    columnInfo.setAutoIncrement(0);
                }
                columnInfos.add(columnInfo);
            }
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }

        return columnInfos;
    }

    private boolean isNumericType(String dataType) {
        if (dataType.contains("int")) {
            return true;
        }

        if (dataType.equalsIgnoreCase("bit")) {
            return true;
        }

        if (dataType.contains("double")) {
            return true;
        }

        if (dataType.contains("decimal")) {
            return true;
        }

        if (dataType.contains("float")) {
            return true;
        }

        return false;
    }
}
