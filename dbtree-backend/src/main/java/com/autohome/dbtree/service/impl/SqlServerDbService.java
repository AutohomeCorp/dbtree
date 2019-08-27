package com.autohome.dbtree.service.impl;

import com.autohome.dbtree.contract.ColumnInfo;
import com.autohome.dbtree.contract.DbInfo;
import com.autohome.dbtree.contract.DelegateRule;
import com.autohome.dbtree.contract.TableInfo;
import com.autohome.dbtree.service.IDbService;
import com.autohome.dbtree.util.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SqlServerDbService implements IDbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlServerDbService.class);

    @Resource
    private ConnManager connManager;

    @Resource
    private Map<java.lang.String, DbInfo> dbInfoMap;

    @Override
    public List<TableInfo> findTablesByDb(String dbName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT so.Name AS tableName, CONVERT(varchar(500), ds.value) AS description \n")
                .append("FROM SysObjects so LEFT JOIN sys.extended_properties ds ON ds.major_id=so.id AND ds.minor_id=0 \n")
                .append("WHERE so.XType='U' AND so.Name <> 'sysdiagrams' \n");
        DbInfo dbInfo = dbInfoMap.get(dbName);
        if (dbInfo.getSplit_table_rules() != null && dbInfo.getSplit_table_rules().size() > 0) {
            for (DelegateRule delegateRule : dbInfo.getSplit_table_rules()) {
                sqlBuilder.append(String.format(" AND (so.Name NOT LIKE '%s' OR so.Name = '%s')", delegateRule.getTable_pattern(), delegateRule.getDelegate_table()));
            }
        }
        sqlBuilder.append(" ORDER BY so.Name");
        try {
            Connection connection = connManager.fetchConnection(dbName);
            return JdbcUtil.executeQuery(connection, dbName, sqlBuilder.toString(), SqlServerDbService::resultSetToTableInfo);
        } catch (Exception ex) {
            throw new RuntimeException("error when findTablesByDb, dbName: " + dbName, ex);
        }
    }

    @Override
    public List<TableInfo> findTablesByName(String dbName, List<String> tableList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT so.Name AS tableName, CONVERT(varchar(500), ds.value) AS description \n")
                .append("FROM SysObjects so LEFT JOIN sys.extended_properties ds ON ds.major_id=so.id AND ds.minor_id=0 \n")
                .append("WHERE so.XType='U' AND so.Name IN ");
        JdbcUtil.appendInClause(sqlBuilder, tableList.size());
        sqlBuilder.append(" ORDER BY so.Name");
        try {
            Connection connection = connManager.fetchConnection(dbName);
            return JdbcUtil.executeQuery(connection, dbName, sqlBuilder.toString(), SqlServerDbService::resultSetToTableInfo,
                    preparedStatement -> {
                        try {
                            int beginIndex = 1;
                            for (String tableName : tableList) {
                                preparedStatement.setString(beginIndex, tableName);
                                beginIndex++;
                            }
                        } catch (Exception ex) {
                            LOGGER.error("error when setString", ex);
                        }
                    });
        } catch (Exception ex) {
            throw new RuntimeException("error when findTablesByName, dbName: " + dbName, ex);
        }
    }

    @Override
    public List<ColumnInfo> findColumnsByTable(String dbName, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT  obj.name AS TableName,  \n")
                .append("        col.colorder AS Num ,  \n")
                .append("        col.name AS Name ,  \n")
                .append("        CONVERT(varchar(500), ep.[value]) AS Comment ,  \n")
                .append("        t.name AS Type ,  \n")
                .append("        col.length AS Size ,  \n")
                .append("        COLUMNPROPERTY(col.id, col.name, 'Scale') AS Scale ,  \n")
                .append("        COLUMNPROPERTY(col.id, col.name, 'PRECISION') AS Prec , \n")
                .append("        CASE WHEN COLUMNPROPERTY(col.id, col.name, 'IsIdentity') = 1 THEN 1  \n")
                .append("             ELSE 0  \n")
                .append("        END AS IsIdentity ,  \n")
                .append("        CASE WHEN EXISTS ( SELECT   1  \n")
                .append("                           FROM     dbo.sysindexes si  \n")
                .append("                                    INNER JOIN dbo.sysindexkeys sik ON si.id = sik.id  \n")
                .append("                                                              AND si.indid = sik.indid  \n")
                .append("                                    INNER JOIN dbo.syscolumns sc ON sc.id = sik.id  \n")
                .append("                                                              AND sc.colid = sik.colid  \n")
                .append("                                    INNER JOIN dbo.sysobjects so ON so.name = si.name  \n")
                .append("                                                              AND so.xtype = 'PK'  \n")
                .append("                           WHERE    sc.id = col.id  \n")
                .append("                                    AND sc.colid = col.colid ) THEN 1  \n")
                .append("             ELSE 0  \n")
                .append("        END AS IsKey ,  \n")
                .append("        CASE WHEN col.isnullable = 1 THEN 1  \n")
                .append("             ELSE 0  \n")
                .append("        END AS IsNullable ,  \n")
                .append("        ISNULL(comm.text, '') AS DefaultValue  \n")
                .append("FROM    dbo.syscolumns col  \n")
                .append("        LEFT  JOIN dbo.systypes t ON col.xtype = t.xusertype  \n")
                .append("        inner JOIN dbo.sysobjects obj ON col.id = obj.id  \n")
                .append("                                         AND obj.xtype = 'U'  \n")
                .append("                                         AND obj.status >= 0  \n")
                .append("        LEFT  JOIN dbo.syscomments comm ON col.cdefault = comm.id  \n")
                .append("        LEFT  JOIN sys.extended_properties ep ON col.id = ep.major_id  \n")
                .append("                                                      AND col.colid = ep.minor_id  \n")
                .append("                                                      AND ep.name = 'MS_Description'  \n")
                .append("        LEFT  JOIN sys.extended_properties epTwo ON obj.id = epTwo.major_id  \n")
                .append("                                                         AND epTwo.minor_id = 0  \n")
                .append("                                                         AND epTwo.name = 'MS_Description'  \n")
                .append("WHERE   obj.name =?  \n")
                .append("ORDER BY col.colorder");

        try {
            Connection connection = connManager.fetchConnection(dbName);

            return JdbcUtil.executeQuery(connection, dbName, sqlBuilder.toString(), SqlServerDbService::resultSetToColumnInfo, preparedStatement -> {
                try {
                    preparedStatement.setString(1, tableName);
                } catch (Exception ex) {
                    LOGGER.error("error when setString", ex);
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException("error when readColumnFromDb, dbName: " + dbName, ex);
        }
    }

    @Override
    public Boolean updateColumnComment(String dbName, String tableName, String column, String comment) {
        StringBuilder dropCommentSpBuilder = new StringBuilder();
        dropCommentSpBuilder.append("EXEC sp_dropextendedproperty \n")
                .append("@name = N'MS_Description',\n")
                .append("@level0type = N'Schema', @level0name = dbo, \n")
                .append("@level1type = N'Table',  @level1name = ?,\n")
                .append("@level2type = N'Column', @level2name = ?");

        StringBuilder addCommentSpBuilder = new StringBuilder();
        addCommentSpBuilder.append("EXEC sp_addextendedproperty \n")
                .append("@name = N'MS_Description', @value = ?,\n")
                .append("@level0type = N'Schema', @level0name = dbo, \n")
                .append("@level1type = N'Table',  @level1name = ?,\n")
                .append("@level2type = N'Column', @level2name = ?");

        try {
            Connection connection = connManager.fetchConnection(dbName);
            try {
                JdbcUtil.execute(connection, dbName, dropCommentSpBuilder.toString(), preparedStatement -> {
                    try {
                        preparedStatement.setString(1, tableName);
                        preparedStatement.setString(2, column);
                    } catch (Exception ex) {
                        LOGGER.error("error when set prepare statement for drop extend property", ex);
                    }
                });
            } catch (Exception ex) {
                LOGGER.warn("drop not exist comment");
            }

            Connection connection1 = connManager.fetchConnection(dbName);
            return JdbcUtil.execute(connection1, dbName, addCommentSpBuilder.toString(), preparedStatement -> {
                try {
                    preparedStatement.setString(1, comment);
                    preparedStatement.setString(2, tableName);
                    preparedStatement.setString(3, column);
                } catch (Exception ex) {
                    LOGGER.error("error when set prepare statement for add extend property", ex);
                }
            });
        } catch (Exception ex) {
            LOGGER.error("error when updateColumnComment, dbName: " + dbName, ex);
            return false;
        }
    }

    @Override
    public Boolean updateTableComment(String dbName, String tableName, String newComment) {
        StringBuilder dropCommentSpBuilder = new StringBuilder();
        dropCommentSpBuilder.append("EXEC sp_dropextendedproperty \n")
                .append("@name = N'MS_Description',\n")
                .append("@level0type = N'Schema', @level0name = dbo, \n")
                .append("@level1type = N'Table',  @level1name = ?");

        StringBuilder addCommentSpBuilder = new StringBuilder();
        addCommentSpBuilder.append("EXEC sp_addextendedproperty \n")
                .append("@name = N'MS_Description', @value = ?,\n")
                .append("@level0type = N'Schema', @level0name = dbo, \n")
                .append("@level1type = N'Table',  @level1name = ?");

        try {
            Connection connection = connManager.fetchConnection(dbName);
            try {
                JdbcUtil.execute(connection, dbName, dropCommentSpBuilder.toString(), preparedStatement -> {
                    try {
                        preparedStatement.setString(1, tableName);
                    } catch (Exception ex) {
                        LOGGER.error("error when set prepare statement for drop extend property", ex);
                    }
                });
            } catch (Exception ex) {
                LOGGER.warn("drop not exist comment");
            }

            Connection connection1 = connManager.fetchConnection(dbName);
            return JdbcUtil.execute(connection1, dbName, addCommentSpBuilder.toString(), preparedStatement -> {
                try {
                    preparedStatement.setString(1, newComment);
                    preparedStatement.setString(2, tableName);
                } catch (Exception ex) {
                    LOGGER.error("error when set prepare statement for add extend property", ex);
                }
            });
        } catch (Exception ex) {
            LOGGER.error("error when updateColumnComment, dbName: " + dbName, ex);
            return false;
        }
    }

    private static List<TableInfo> resultSetToTableInfo(ResultSet resultSet) {
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(resultSet.getString("tableName"));
                tableInfo.setDescription(resultSet.getString("description"));
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
                columnInfo.setColumnName(resultSet.getString("Name"));
                columnInfo.setColumnComment(resultSet.getString("Comment"));
                Integer precision = resultSet.getInt("Prec");
                columnInfo.setPrecision(precision);
                Integer scale = resultSet.getInt("Scale");
                columnInfo.setScale(scale);
                String dataType = resultSet.getString("Type");
                columnInfo.setDataType(dataType);
                String columnType = null;
                if (dataType.equalsIgnoreCase("varchar") || dataType.equalsIgnoreCase("nvarchar")) {
                    if (precision == -1) {
                        columnType = dataType + "(max)";
                    } else {
                        columnType = dataType + "(" + precision.toString() + ")";
                    }
                } else if (dataType.equalsIgnoreCase("decimal")) {
                    columnType = dataType + "(" + precision.toString() + "," + scale.toString() + ")";
                } else {
                    columnType = dataType;
                }
                //varchar(200)
                columnInfo.setColumnType(columnType);
                String nullable = null;
                Integer isNullable = resultSet.getInt("IsNullable");
                if (isNullable == 0) {
                    nullable = "NO";
                } else {
                    nullable = "YES";
                }
                columnInfo.setNullable(nullable);
                columnInfo.setColumnDefault(resultSet.getString("DefaultValue"));
                columnInfo.setPrimaryKey(resultSet.getInt("IsKey"));
                columnInfo.setAutoIncrement(resultSet.getInt("IsIdentity"));

                columnInfos.add(columnInfo);
            }
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }

        return columnInfos;
    }
}
