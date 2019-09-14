package com.autohome.dbtree.service;

import com.autohome.dbtree.contract.*;
import com.autohome.dbtree.dao.mysql.dbtree.domain.TreeNode;

import java.util.List;

public interface ITreeService {

    boolean addDb(String dbName);

    boolean deleteFolder(Long id);

    boolean updateDb(String dbName);

    boolean deleteDb(String dbName);

    boolean moveNodeTo(List<Long> nodeIdList, Long newParentId);

    List<TableInfo> tableList(String dbName);

    TableInfo oneTable(String dbName, String tableName);

    List<ColumnInfo> columnList(String dbName, String tableName);

    List<TreeNode> findAllDbNodes();

    List<TreeNode> findChildren(Long id);

    List<TreeNode> findTableChildren(Long id);

    TreeNode addFolderNode(String folderName, Long parentId);

    Boolean rename(Long id, String nodeName);

    TableDetail tableDetail(Long tableId);

    List<TableInfo> folderTables(Long folderId);

    FolderClassifyInfo folderClassifyInfo(Long folderId);

    Boolean updateColumnComment(Long nodeId, String columnName, String newComment);

    Boolean updateTableComment(String dbName, String tableName, String newComment);
}
