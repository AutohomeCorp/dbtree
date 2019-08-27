package com.autohome.dbtree.dao.mysql.dbtree.mapper;

import com.autohome.dbtree.dao.mysql.dbtree.domain.SyncTaskLock;

public interface SyncTaskLockMapper {
    int insert(SyncTaskLock record);

    SyncTaskLock selectByPrimaryKey(Integer id);

    int remove(Integer lockId);
}