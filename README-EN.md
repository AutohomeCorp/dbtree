## DBTree Introduction

English | [简体中文](./README.md)

DBTree is a light web tool for browsing database table definitions. Compared to phpMyAdmin, DBTree only 
focus on browsing table information and maintaining comment. You can use it to classify your tables as folders and 
update comment handily.  

## Features

* browse important schema information
* show only one table delegate if the table is split into hundreds or thousands.
* update table comment, column comment online handily.
* support mysql, sql server
* classify tables as folders.

## Demo

<img src="./dbtree_demo.gif" />

## Why this tool

In daily development, database communication is one of the most important and frequent part, our team found that no matter database 
client like MSS Management Studio, phpMyAdmin or database design software like Power Designer, it is not convenient to browse or share 
table definition and comment. Especially when there are thousands of sub-tables, the client is really tough. So we design this tool and we 
love this tool, hope it would help you too. 

comment friendly and classify your table is good for all.

## config

* database server config

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
the password is plain.

* database config

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
db-config.json is a json map content, the key must be as same as database name(db_name). You can use split_table_rules to specify the 
table split pattern, the example means using rule to represent all rule_% tables.

## Deploy

This project use springboot2.x + vue-element-template + mybatis。dbtree-vue resources will be package into dbtree-backend/src/main/resources/public. 
You need only deploy dbtree-backend/target/dbtree.jar.  

mysql schema folder：dbtree-backend/doc/schema

JDK version 1.8

The tool only deploy in develop environment, you don't need to deploy it in production, it is not safe.

## Thanks To

- [vue-admin-template](https://github.com/PanJiaChen/vue-admin-template)


