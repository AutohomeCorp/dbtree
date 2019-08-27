package com.autohome.dbtree.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class TableInfo {

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "表说明")
    private String description;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
