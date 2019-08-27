package com.autohome.dbtree.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@MapperScan(value = "com.autohome.dbtree.dao.mysql.dbtree.mapper", sqlSessionFactoryRef = "dbTreeSqlSessionFactory")
public class DbTreeMybatisConfig {

    @Value("${druid.datasource.dbtree.mapperLocations}")
    private String mapperLocations;

    @Bean(name = "dbTreeDataSource", destroyMethod = "close")
    @ConfigurationProperties(prefix = "druid.datasource.dbtree")
    public DruidDataSource dbTreeDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "dbTreeSqlSessionFactory")
    public SqlSessionFactory dbTreeSqlSessionFactory() throws Exception {
        PageInterceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(new Properties());
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dbTreeDataSource());
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageInterceptor});

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(this.mapperLocations));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            return sqlSessionFactoryBean.getObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public PlatformTransactionManager dbTreeTransactionManager() throws Exception {
        return new DataSourceTransactionManager(dbTreeDataSource());
    }
}
