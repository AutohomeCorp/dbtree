package com.autohome.dbtree.contract;

import java.util.List;

public class DbInfo {

    private String db_server;

    private String db_name;

    private List<DelegateRule> split_table_rules;

    public String getDb_server() {
        return db_server;
    }

    public void setDb_server(String db_server) {
        this.db_server = db_server;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public List<DelegateRule> getSplit_table_rules() {
        return split_table_rules;
    }

    public void setSplit_table_rules(List<DelegateRule> split_table_rules) {
        this.split_table_rules = split_table_rules;
    }
}
