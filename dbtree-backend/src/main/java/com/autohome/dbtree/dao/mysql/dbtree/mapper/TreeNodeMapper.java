package com.autohome.dbtree.dao.mysql.dbtree.mapper;

import com.autohome.dbtree.dao.mysql.dbtree.domain.TreeNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TreeNodeMapper {

    TreeNode selectByPrimaryKey(Long id);

    void add(TreeNode treeNode);

    Integer removeById(Long id);

    List<TreeNode> findChildren(Long id);

    Integer updateParent(@Param("idList") List<Long> idList,
                         @Param("newParentId") Long newParentId);

    List<TreeNode> findChildrenByDb(String dbName);

    TreeNode findSingleDbNode(String dbName);

    List<TreeNode> findAllDbNodes();

    Integer rename(@Param("id") Long id,
                   @Param("nodeName") String nodeName);
}