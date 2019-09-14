package com.autohome.dbtree.service;

import java.util.List;

public interface IMybatisCodeGenerateService {

    String execute(String domainPackage, String mapperPackage, String dbName, List<String> tableList, Boolean useActualColumnNames);
}
