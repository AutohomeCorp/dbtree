package com.autohome.dbtree.service.impl;

import com.autohome.dbtree.contract.*;
import com.autohome.dbtree.dao.mysql.dbtree.domain.TreeNode;
import com.autohome.dbtree.dao.mysql.dbtree.mapper.TreeNodeMapper;
import com.autohome.dbtree.service.IDbService;
import com.autohome.dbtree.service.ITreeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TreeService implements ITreeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(TreeService.class);

    @Resource
    private Map<String, DbInfo> dbInfoMap;

    @Resource
    private Map<String, DbServer> dbServerMap;

    @Resource
    private MysqlDbService mysqlDbService;

    @Resource
    private SqlServerDbService sqlServerDbService;

    @Resource
    private TreeNodeMapper treeNodeMapper;

    @Override
    public boolean addDb(String dbName) {
        IDbService dbService = chooseDbService(dbName);
        TreeNode dbNode = new TreeNode();
        dbNode.setNode_name(dbName);
        dbNode.setParent_id(0L);
        dbNode.setFolder(1);
        dbNode.setDb_name(dbName);

        treeNodeMapper.add(dbNode);

        Long dbNodeId = dbNode.getId();

        List<TableInfo> tableList = dbService.findTablesByDb(dbName);
        for (TableInfo tableInfo : tableList) {
            TreeNode tableNode = new TreeNode();
            tableNode.setDb_name(dbName);
            tableNode.setParent_id(dbNodeId);
            tableNode.setFolder(0);
            tableNode.setNode_name(tableInfo.getTableName());
            treeNodeMapper.add(tableNode);
        }

        return true;
    }

    @Override
    public boolean deleteFolder(Long id) {
        List<TreeNode> children = treeNodeMapper.findChildren(id);
        if (children.size() > 0) {
            return false;
        }
        return treeNodeMapper.removeById(id) > 0;
    }

    @Override
    public boolean updateDb(String dbName) {
        try {
            List<TreeNode> nodes = treeNodeMapper.findChildrenByDb(dbName);
            List<TreeNode> tableNodes = nodes.stream()
                    .filter(node -> node.getFolder() == 0)
                    .collect(Collectors.toList());
            IDbService dbService = chooseDbService(dbName);
            List<TableInfo> tableInfoList = dbService.findTablesByDb(dbName);
            //delete redundant nodes
            List<TreeNode> needDeleteNodes = tableNodes.stream().filter(node -> tableInfoList.stream()
                    .noneMatch(tableInfo -> tableInfo.getTableName().equalsIgnoreCase(node.getNode_name())))
                    .collect(Collectors.toList());
            for (TreeNode treeNode : needDeleteNodes) {
                treeNodeMapper.removeById(treeNode.getId());
            }
            //add new nodes
            List<TableInfo> needAddTables = tableInfoList.stream().filter(tableInfo -> tableNodes.stream()
                    .noneMatch(node -> node.getNode_name().equalsIgnoreCase(tableInfo.getTableName())))
                    .collect(Collectors.toList());
            TreeNode dbNode = treeNodeMapper.findSingleDbNode(dbName);
            for (TableInfo tableInfo : needAddTables) {
                TreeNode treeNode = new TreeNode();
                treeNode.setParent_id(dbNode.getId());
                treeNode.setNode_name(tableInfo.getTableName());
                treeNode.setFolder(0);
                treeNode.setDb_name(dbName);
                treeNodeMapper.add(treeNode);
            }
        } catch (Exception ex) {
            LOGGER.error("error when updateDb, db: " + dbName, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteDb(String dbName) {
        return false;
    }

    @Override
    public boolean moveNodeTo(List<Long> nodeIdList, Long newParentId) {
        Integer count = treeNodeMapper.updateParent(nodeIdList, newParentId);
        return count > 0;
    }

    @Override
    public List<TableInfo> tableList(String dbName) {
        IDbService dbService = chooseDbService(dbName);
        return dbService.findTablesByDb(dbName);
    }

    @Override
    public TableInfo oneTable(String dbName, String tableName) {
        IDbService dbService = chooseDbService(dbName);
        List<TableInfo> tableInfoList = dbService.findTablesByName(dbName, Collections.singletonList(tableName));
        return tableInfoList.get(0);
    }

    @Override
    public List<ColumnInfo> columnList(String dbName, String tableName) {
        IDbService dbService = chooseDbService(dbName);
        return dbService.findColumnsByTable(dbName, tableName);
    }

    @Override
    public List<TreeNode> findAllDbNodes() {
        return treeNodeMapper.findAllDbNodes();
    }

    @Override
    public List<TreeNode> findChildren(Long id) {
        return treeNodeMapper.findChildren(id);
    }

    @Override
    public List<TreeNode> findTableChildren(Long id) {
        List<TreeNode> allChildren = findChildren(id);
        return allChildren.stream().filter(child -> child.getFolder() == 0).collect(Collectors.toList());
    }

    @Override
    public FolderClassifyInfo folderClassifyInfo(Long folderId) {
        TreeNode folderNode = this.treeNodeMapper.selectByPrimaryKey(folderId);
        List<TreeNode> folderTableNodes = this.treeNodeMapper.findChildren(folderId).stream()
                .filter(x -> x.getFolder() == 0).collect(Collectors.toList());
        TreeNode dbNode = this.treeNodeMapper.findSingleDbNode(folderNode.getDb_name());
        List<TreeNode> notClassifiedNodes = this.treeNodeMapper.findChildren(dbNode.getId()).stream()
                .filter(x-> x.getFolder() == 0).collect(Collectors.toList());

        List<TreeNode> tables = new ArrayList<>();
        tables.addAll(folderTableNodes);
        tables.addAll(notClassifiedNodes);
        FolderClassifyInfo folderClassifyInfo = new FolderClassifyInfo();
        folderClassifyInfo.setTables(tables.stream().map(TreeNodeVo::fromTreeNode).collect(Collectors.toList()));
        folderClassifyInfo.setNotClassifiedIdList(notClassifiedNodes.stream().map(TreeNode::getId).collect(Collectors.toList()));

        return folderClassifyInfo;
    }

    @Override
    public TreeNode addFolderNode(String folderName, Long parentId) {
        TreeNode parentNode = this.treeNodeMapper.selectByPrimaryKey(parentId);
        TreeNode treeNode = new TreeNode();
        treeNode.setFolder(1);
        treeNode.setNode_name(folderName);
        treeNode.setParent_id(parentId);
        treeNode.setDb_name(parentNode.getDb_name());
        try {
            this.treeNodeMapper.add(treeNode);
            return treeNode;
        } catch (Exception ex) {
            LOGGER.error("error when addFolderNode", ex);
            return null;
        }
    }

    @Override
    public Boolean rename(Long id, String nodeName) {
        return this.treeNodeMapper.rename(id, nodeName) > 0;
    }

    @Override
    public Boolean updateColumnComment(Long nodeId, String columnName, String newComment) {
        TreeNode node = this.treeNodeMapper.selectByPrimaryKey(nodeId);
        IDbService dbService = this.chooseDbService(node.getDb_name());
        return dbService.updateColumnComment(node.getDb_name(), node.getNode_name(), columnName, newComment);
    }

    @Override
    public Boolean updateTableComment(String dbName, String tableName, String newComment) {
        IDbService dbService = this.chooseDbService(dbName);
        return dbService.updateTableComment(dbName, tableName, newComment);
    }

    public List<TableInfo> folderTables(Long folderId) {
        TreeNode folderNode = treeNodeMapper.selectByPrimaryKey(folderId);
        List<TreeNode> nodes = findTableChildren(folderId);
        if(nodes.size() == 0) {
            return new ArrayList<>();
        }
        IDbService dbService = chooseDbService(folderNode.getDb_name());
        return dbService.findTablesByName(folderNode.getDb_name(), nodes.stream().map(TreeNode::getNode_name).collect(Collectors.toList()));
    }

    public TableDetail tableDetail(Long tableId) {
        TreeNode treeNode = treeNodeMapper.selectByPrimaryKey(tableId);
        IDbService dbService = chooseDbService(treeNode.getDb_name());
        List<TableInfo> tableInfoList = dbService.findTablesByName(treeNode.getDb_name(), Collections.singletonList(treeNode.getNode_name()));
        TableDetail tableDetail = new TableDetail();
        tableDetail.setTableInfo(tableInfoList.get(0));
        List<ColumnInfo> columnInfoList = dbService.findColumnsByTable(treeNode.getDb_name(), treeNode.getNode_name());
        tableDetail.setColumnInfoList(columnInfoList);
        return tableDetail;
    }

    private IDbService chooseDbService(String dbName) {
        DbInfo dbInfo = dbInfoMap.get(dbName);
        DbServer dbServer = dbServerMap.get(dbInfo.getDb_server());
        if (dbServer.getDb_type().equalsIgnoreCase("mysql")) {
            return mysqlDbService;
        }

        if (dbServer.getDb_type().equalsIgnoreCase("sqlserver")) {
            return sqlServerDbService;
        }

        throw new IllegalArgumentException("unknown db_type: " + dbServer.getDb_type());
    }
}
