package com.autohome.dbtree.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JdbcUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);

    public static <T> T executeQuery(Connection connection, String dbName, String sql, Function<ResultSet, T> resultSetFunction, Consumer<PreparedStatement> preparedStatementFunction) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (preparedStatementFunction != null) {
                preparedStatementFunction.accept(preparedStatement);
            }
            resultSet = preparedStatement.executeQuery();
            return resultSetFunction.apply(resultSet);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception ex) {
                LOGGER.error("error when close resultSet", ex);
            }

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception ex) {
                LOGGER.error("error when close statement", ex);
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
                LOGGER.error("error when close connection", ex);
            }
        }
    }

    public static boolean execute(Connection connection, String dbName, String sql, Consumer<PreparedStatement> preparedStatementFunction) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (preparedStatementFunction != null) {
                preparedStatementFunction.accept(preparedStatement);
            }
            preparedStatement.execute();
            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception ex) {
                LOGGER.error("error when close statement", ex);
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
                LOGGER.error("error when close connection", ex);
            }
        }
    }


    public static <T> T executeQuery(Connection connection, String dbName, String sql, Function<ResultSet, T> resultSetFunction) {
        return executeQuery(connection, dbName, sql, resultSetFunction, null);
    }

    public static void appendInClause(StringBuilder sqlBuilder, int size) {
        sqlBuilder.append(" (");
        List<String> placeHolderList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            placeHolderList.add("?");
        }
        sqlBuilder.append(String.join(",", placeHolderList));
        sqlBuilder.append(")");
    }
}
