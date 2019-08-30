## DBTree简介

简体中文 | [English](./README-EN.md)

DBTree是一个WEB版的轻量数据库表结构查看及管理工具，相比phpMyAdmin, DBTree只专注于方便开发查看表结构信息和
注释维护。通过树形展示库表结构，可以对表进行自定义归类，在线更新注释。

## Features

* 展示表常用信息
* 分表只展示其中一个表结构
* 在线维护表注释，字段注释
* 支持mysql, sqlserver
* 对表进行目录分类

## 功能演示

<img src="./dbtree_demo.gif" />

## 为何写这个小工具

在不断的迭代过程中，数据库是开发之间沟通非常频繁且重要的一个环节，我们发现无论是mss management studio，phpMyAdmin这类数据库客户端还是
类似Power Designer的数据库设计软件对表信息共享，注释维护这两个需求来说用起来都非常不便利，尤其是有上千个分表的时候，客户端用起来还是非常难受的。
所以写了这个网页小工具，希望对有同样需求的人有所帮助。  

维护好注释，对表进行合理的业务归类，方便你我他，oh, yeah!

## 配置

* 服务器配置

dbtree-backend/src/main/resources/dbconfig/db-server.json
```json
{
  "mysql-127.0.0.1": {
    "db_type": "mysql",
    "host": "127.0.0.1",
    "port": 3306,
    "user": "root",
    "password": "123456"
  },
  "sqlserver-127.0.0.2": {
    "db_type": "sqlserver",
    "host": "127.0.0.2",
    "port": 1433,
    "user": "root",
    "password":"123456"
  }
}
```
密码是明文配置

* 数据库配置

dbtree-backend/src/main/resources/dbconfig/db-config.json
```json
{
  "db_1": {
    "db_name": "db_1",
    "db_server": "mysql-127.0.0.1",
    "split_table_rules": [
      {
        "delegate_table": "rule",
        "table_pattern": "rule_%"
      }
    ]
  },
  "db_2": {
     "db_name": "db_2",
     "db_server": "sqlserver-127.0.0.2"
  }
}
```
db-config.json配置文件内容是map结构的json数据，其中key必须和库名(db_name)一致。split_table_rules是指定分表策略，例子的意思是用 rule 代替所有rule_%的表。

## 部署

项目使用 springboot2.x + vue-element-template + mybatis。dbtree-vue资源会打包进dbtree-backend/src/main/resources/public下。直接部署
dbtree-backend/target/dbtree.jar就可以了。  

mysql表创建语句见：dbtree-backend/doc/schema目录

JDK使用1.8

这个工具主要给开发人员使用，部署到测试就可以了，没必要部署到线上，也不安全。

## 致谢

- [vue-admin-template](https://github.com/PanJiaChen/vue-admin-template)


