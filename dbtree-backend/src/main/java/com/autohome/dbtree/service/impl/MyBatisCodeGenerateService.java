package com.autohome.dbtree.service.impl;

import com.autohome.dbtree.config.MybatisCodeGeneratorConfig;
import com.autohome.dbtree.contract.DbInfo;
import com.autohome.dbtree.contract.DbServer;
import com.autohome.dbtree.service.IMybatisCodeGenerateService;
import org.joda.time.DateTime;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MyBatisCodeGenerateService implements IMybatisCodeGenerateService {

    @Resource
    private Map<String, DbInfo> dbInfoMap;

    @Resource
    private Map<String, DbServer> dbServerMap;

    @Resource
    private MybatisCodeGeneratorConfig mybatisCodeGeneratorConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisCodeGenerateService.class);

    @Override
    public String execute(String domainPackage, String mapperPackage, String dbName, List<String> tableList, Boolean useActualColumnNames) {
        String folderName = dbName + "_" + DateTime.now().toString("yyyyMMdd-HHmmss");
        String absoluteFolderPath = mybatisCodeGeneratorConfig.getMybatisBaseFolder() + File.separator + folderName;
        File directory = new File(absoluteFolderPath);
        directory.mkdirs();
        directory.setWritable(true);

        List<String> warnings = new ArrayList<>();
        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("DB2Tables");
        context.setTargetRuntime("MyBatis3");

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        commentGeneratorConfiguration.addProperty("useActualColumnNames", "false");

        /*数据库链接URL，用户名、密码 */
        DbInfo dbInfo = dbInfoMap.get(dbName);
        DbServer dbServer = dbServerMap.get(dbInfo.getDb_server());
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        if(dbServer.getDb_type().equalsIgnoreCase("mysql")) {
            jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
            jdbcConnectionConfiguration.setConnectionURL(String.format("jdbc:mysql://%s:%s/%s", dbServer.getHost(), dbServer.getPort(), dbName));
        } else if (dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
            jdbcConnectionConfiguration.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            jdbcConnectionConfiguration.setConnectionURL(String.format("jdbc:sqlserver://%s:%s;database=%s", dbServer.getHost(), dbServer.getPort(), dbName));
        }
        jdbcConnectionConfiguration.setUserId(dbServer.getUser());
        jdbcConnectionConfiguration.setPassword(dbServer.getPassword());
        jdbcConnectionConfiguration.addProperty("useInformationSchema", "true");

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");

        /*生成模型的包名和位置*/
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        //javaModelGeneratorConfiguration.setTargetPackage(String.format("%s.%s.%s.domain", daoPackage, dbServer.getDb_type(), dbName));
        //javaModelGeneratorConfiguration.setTargetPackage(basePackage + ".domain");
        javaModelGeneratorConfiguration.setTargetPackage(domainPackage);
        javaModelGeneratorConfiguration.setTargetProject(absoluteFolderPath);
        javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
        javaModelGeneratorConfiguration.addProperty("trimStrings", "true");

        /*生成映射文件的包名和位置*/
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        //sqlMapGeneratorConfiguration.setTargetPackage(String.format("%s.%s.%s.mapping", daoPackage, dbServer.getDb_type(), dbName));
        sqlMapGeneratorConfiguration.setTargetPackage("resources.mapping");
        sqlMapGeneratorConfiguration.setTargetProject(absoluteFolderPath);
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");

        /*生成DAO的包名和位置*/
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        //javaClientGeneratorConfiguration.setTargetPackage(String.format("%s.%s.%s.mapper", daoPackage, dbServer.getDb_type(), dbName));
        //javaClientGeneratorConfiguration.setTargetPackage(basePackage + ".mapper");
        javaClientGeneratorConfiguration.setTargetPackage(mapperPackage);
        javaClientGeneratorConfiguration.setTargetProject(absoluteFolderPath);
        javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        //Table Configuration
        for (String tableName : tableList) {
            TableConfiguration tableConfiguration = new TableConfiguration(context);
            tableConfiguration.setTableName(tableName);
            tableConfiguration.addProperty("useActualColumnNames", (useActualColumnNames != null && useActualColumnNames) ? "true" : "false");
            tableConfiguration.setCountByExampleStatementEnabled(false);
            tableConfiguration.setUpdateByExampleStatementEnabled(false);
            tableConfiguration.setDeleteByExampleStatementEnabled(false);
            tableConfiguration.setSelectByExampleStatementEnabled(false);
            tableConfiguration.setInsertStatementEnabled(true);
            tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(true);
            tableConfiguration.setSelectByPrimaryKeyStatementEnabled(true);
            tableConfiguration.setDeleteByPrimaryKeyStatementEnabled(true);
            /*GeneratedKey generatedKey = new GeneratedKey("id", "mysql", true, null);
            tableConfiguration.setGeneratedKey(generatedKey);*/

            context.addTableConfiguration(tableConfiguration);
        }

        Configuration config = new Configuration();
        if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
            config.addClasspathEntry(mybatisCodeGeneratorConfig.getMysqlConnector());
        } else if (dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
            config.addClasspathEntry(mybatisCodeGeneratorConfig.getSqlserverConnector());
        }
        config.addContext(context);

        boolean overwrite = true;
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        try {
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            //zip
            String zipFile = absoluteFolderPath + ".zip";
            ZipUtil.pack(new File(absoluteFolderPath), new File(zipFile));
            return folderName + ".zip";
        } catch (Exception ex) {
            LOGGER.error("error when generate mybatis code", ex);
            return null;
        }
    }
}
