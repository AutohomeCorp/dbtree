package com.autohome.dbtree.service;

import java.util.List;

public interface ITableExportService {

    String exportMarkdown(String dbName, List<String> tableList);
}
