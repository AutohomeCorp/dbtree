package com.autohome.dbtree.dao.mysql.dbtree.domain;

import java.util.Date;

public class SyncTaskLock {
    private Integer id;

    private Integer lock_id;

    private Date created_stime;

    private Date modified_stime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLock_id() {
        return lock_id;
    }

    public void setLock_id(Integer lock_id) {
        this.lock_id = lock_id;
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