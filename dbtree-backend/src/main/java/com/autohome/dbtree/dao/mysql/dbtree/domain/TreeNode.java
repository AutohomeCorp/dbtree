package com.autohome.dbtree.dao.mysql.dbtree.domain;

import java.util.Date;

public class TreeNode {
    private Long id;

    private String db_name;

    private Integer folder;

    private String node_name;

    private Long parent_id;

    private Date created_stime;

    private Date modified_stime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name == null ? null : db_name.trim();
    }

    public Integer getFolder() {
        return folder;
    }

    public void setFolder(Integer folder) {
        this.folder = folder;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name == null ? null : node_name.trim();
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public Date getCreated_stime() {
        return created_stime;
    }

    public void setCreated_stime(Date created_stime) {
        this.created_stime = created_stime;
    }

    public Date getModified_stime() {
        return modified_stime;
    }

    public void setModified_stime(Date modified_stime) {
        this.modified_stime = modified_stime;
    }
}