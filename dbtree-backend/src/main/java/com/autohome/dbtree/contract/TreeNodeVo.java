package com.autohome.dbtree.contract;

import com.autohome.dbtree.dao.mysql.dbtree.domain.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class TreeNodeVo {

    @ApiModelProperty(value = "所属数据库")
    private String database;

    @ApiModelProperty(value = "节点名")
    private String node_name;

    @ApiModelProperty(value = "是否目录节点")
    private Boolean leaf;

    @ApiModelProperty(value = "节点ID")
    private Long id;

    @ApiModelProperty(value = "父亲节点ID")
    private Long parent_id;

    @ApiModelProperty(value = "是否在编辑")
    private Boolean edit;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public static TreeNodeVo fromTreeNode(TreeNode treeNode) {
        TreeNodeVo treeNodeVo = new TreeNodeVo();
        treeNodeVo.setId(treeNode.getId());
        treeNodeVo.setNode_name(treeNode.getNode_name());
        treeNodeVo.setParent_id(treeNode.getParent_id());
        treeNodeVo.setLeaf(treeNode.getFolder() == 0);
        treeNodeVo.setEdit(false);
        treeNodeVo.setDatabase(treeNode.getDb_name());
        return treeNodeVo;
    }
}
