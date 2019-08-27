package com.autohome.dbtree.service.impl;

import com.autohome.dbtree.dao.mysql.dbtree.domain.SyncTaskLock;
import com.autohome.dbtree.dao.mysql.dbtree.mapper.SyncTaskLockMapper;

import javax.annotation.Resource;

public class LockService {

    @Resource
    private SyncTaskLockMapper syncTaskLockMapper;

    public Boolean tryLock() {
        SyncTaskLock syncTaskLock = new SyncTaskLock();
        syncTaskLock.setLock_id(1);
        try {
            syncTaskLockMapper.insert(syncTaskLock);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Boolean unLock() {
        return syncTaskLockMapper.remove(1) > 0;
    }
}
