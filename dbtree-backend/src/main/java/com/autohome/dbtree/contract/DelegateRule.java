package com.autohome.dbtree.contract;

public class DelegateRule {

    private String delegate_table;

    private String table_pattern;

    public String getDelegate_table() {
        return delegate_table;
    }

    public void setDelegate_table(String delegate_table) {
        this.delegate_table = delegate_table;
    }

    public String getTable_pattern() {
        return table_pattern;
    }

    public void setTable_pattern(String table_pattern) {
        this.table_pattern = table_pattern;
    }
}
