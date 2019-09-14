package com.autohome.dbtree.service.impl;

import com.autohome.dbtree.contract.ColumnInfo;
import com.autohome.dbtree.contract.TableInfo;
import com.autohome.dbtree.service.ITableExportService;
import com.autohome.dbtree.service.ITreeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TableExportService implements ITableExportService {

    @Resource
    private ITreeService treeService;

    @Override
    public String exportMarkdown(String dbName, List<String> tableList) {
        StringBuilder markdownBuilder = new StringBuilder();
        for (String table : tableList) {
            TableInfo tableInfo = treeService.oneTable(dbName, table);
            List<ColumnInfo> columnInfoList = treeService.columnList(dbName, table);
            markdownBuilder.append("\n\n").append("## ").append(table).append(": ").append(tableInfo.getDescription()).append("\n");
            markdownBuilder.append("序号|字段名|类型|自增|可空|主键|默认值|注释").append("\n")
                    .append("---|---|---|---|---|---|---|---");
            for (int i = 0; i < columnInfoList.size(); i++) {
                ColumnInfo columnInfo = columnInfoList.get(i);
                markdownBuilder.append("\n").append(i).append("|")
                        .append(columnInfo.getColumnName()).append("|")
                        .append(columnInfo.getColumnType()).append("|")
                        .append(columnInfo.getAutoIncrement()).append("|")
                        .append(columnInfo.getNullable()).append("|")
                        .append(columnInfo.getPrimaryKey()).append("|")
                        .append(columnInfo.getColumnDefault() == null ? "" : columnInfo.getColumnDefault()).append("|")
                        .append(columnInfo.getColumnComment());
            }
        }

        return markdownBuilder.toString();
    }
}
