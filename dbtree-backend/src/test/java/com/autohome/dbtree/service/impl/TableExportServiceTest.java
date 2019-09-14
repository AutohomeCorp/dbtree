package com.autohome.dbtree.service.impl;

import com.autohome.dbtree.contract.ColumnInfo;
import com.autohome.dbtree.contract.TableInfo;
import com.autohome.dbtree.service.ITreeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TableExportServiceTest {

    @Mock
    private ITreeService treeService;

    @InjectMocks
    private TableExportService tableExportService;

    @Test
    public void exportMarkdownTest() {
        List<ColumnInfo> columnInfoList1 = new ArrayList<>();
        ColumnInfo columnInfo11 = new ColumnInfo();
        columnInfo11.setColumnName("id");
        columnInfo11.setColumnType("int(11)");
        columnInfo11.setAutoIncrement(1);
        columnInfo11.setNullable("NO");
        columnInfo11.setPrimaryKey(1);
        columnInfo11.setColumnDefault("");
        columnInfo11.setColumnComment("自增id");
        columnInfoList1.add(columnInfo11);

        ColumnInfo columnInfo12 = new ColumnInfo();
        columnInfo12.setColumnName("name");
        columnInfo12.setColumnType("varchar(100)");
        columnInfo12.setAutoIncrement(0);
        columnInfo12.setNullable("NO");
        columnInfo12.setPrimaryKey(0);
        columnInfo12.setColumnDefault("");
        columnInfo12.setColumnComment("名称");
        columnInfoList1.add(columnInfo12);

        Mockito.doReturn(columnInfoList1).when(treeService).columnList(Mockito.anyString(), Mockito.anyString());
        TableInfo tableInfo1 = new TableInfo();
        tableInfo1.setTableName("table");
        tableInfo1.setDescription("注释1");
        Mockito.doReturn(tableInfo1).when(treeService).oneTable("db", "table");

        TableInfo tableInfo2 = new TableInfo();
        tableInfo2.setTableName("table2");
        tableInfo2.setDescription("注释2");
        Mockito.doReturn(tableInfo2).when(treeService).oneTable("db", "table2");

        String result = tableExportService.exportMarkdown("db", Arrays.asList("table", "table2"));

        assertEquals("\n" +
                "\n" +
                "## table: 注释1\n" +
                "序号|字段名|类型|自增|可空|主键|默认值|注释\n" +
                "---|---|---|---|---|---|---|---\n" +
                "0|id|int(11)|1|NO|1||自增id\n" +
                "1|name|varchar(100)|0|NO|0||名称\n" +
                "\n" +
                "## table2: 注释2\n" +
                "序号|字段名|类型|自增|可空|主键|默认值|注释\n" +
                "---|---|---|---|---|---|---|---\n" +
                "0|id|int(11)|1|NO|1||自增id\n" +
                "1|name|varchar(100)|0|NO|0||名称", result);
    }
}