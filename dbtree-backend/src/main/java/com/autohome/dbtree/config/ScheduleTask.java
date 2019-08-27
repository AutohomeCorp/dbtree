package com.autohome.dbtree.config;

import com.autohome.dbtree.contract.DbInfo;
import com.autohome.dbtree.dao.mysql.dbtree.domain.TreeNode;
import com.autohome.dbtree.service.ITreeService;
import com.autohome.dbtree.service.impl.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    @Resource
    private LockService lockService;

    @Resource
    private Map<String, DbInfo> dbInfoMap;

    @Resource
    private ITreeService treeService;

    @Scheduled(fixedDelay = 1000 * 60 * 5, initialDelay = 1000 * 10)
    public void syncTableTask() {
        Boolean haveLock = lockService.tryLock();
        if(!haveLock) {
            return;
        }
        try {
            List<TreeNode> databaseNodes = treeService.findAllDbNodes();
            for (Map.Entry<String, DbInfo> dbInfoEntry : dbInfoMap.entrySet()) {
                try {
                    String dbName = dbInfoEntry.getKey();
                    boolean exist = databaseNodes.stream().anyMatch(x -> x.getNode_name().equalsIgnoreCase(dbName));
                    if (exist) {
                        treeService.updateDb(dbName);
                    } else {
                        treeService.addDb(dbName);
                    }
                } catch (Exception ex) {
                    LOGGER.error("error when sync table for db: " + dbInfoEntry.getKey(), ex);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("error when syncTableTask", ex);
        } finally {
            lockService.unLock();
        }
    }
}
