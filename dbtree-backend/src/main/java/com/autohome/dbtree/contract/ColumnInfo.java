package com.autohome.dbtree.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ColumnInfo {

    @ApiModelProperty(value = "列名")
    private String columnName;

    @ApiModelProperty(value = "列数据类型，如: varchar")
    private String dataType;

    private String maxLength;

    @ApiModelProperty(value = "列注释")
    private String columnComment;

    @ApiModelProperty(value = "列默认值")
    private String columnDefault;

    @ApiModelProperty(value = "列是否可空")
    private String nullable;

    @ApiModelProperty(value = "是否主键")
    private Integer primaryKey;

    @ApiModelProperty(value = "额外说明")
    private String extra;

    @ApiModelProperty(value = "列类型。如: varchar(200)")
    private String columnType;

    @ApiModelProperty(value = "是否自增")
    private Integer autoIncrement;

    @ApiModelProperty(value = "字段精度")
    private Integer precision;

    @ApiModelProperty(value = "小数位数")
    private Integer scale;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Integer getAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(Integer autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
