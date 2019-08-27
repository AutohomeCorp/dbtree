package com.autohome.dbtree.controller;

import com.autohome.dbtree.contract.*;
import com.autohome.dbtree.dao.mysql.dbtree.domain.TreeNode;
import com.autohome.dbtree.service.ITreeService;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "/tree")
@RestController
@RequestMapping(value = "/tree")
public class TreeController {

    @Resource
    private ITreeService treeService;

    @ApiOperation(value = "查询目录下所有表", notes = "查询目录下所有表，不包含目录节点，不会递归往下查")
    @RequestMapping(value = "/tableList", method = RequestMethod.GET)
    public Protocol<List<TableInfo>> tableList(@ApiParam(value = "目录ID", required = true)
                                               @RequestParam(value = "folderId", required = true) Long folderId) {
        List<TableInfo> children = treeService.folderTables(folderId);
        return new Protocol<>(children);
    }

    @ApiOperation(value = "查询目录下所有节点", notes = "查询目录下所有节点，包含目录节点，不会递归往下查")
    @RequestMapping(value = "/children", method = RequestMethod.GET)
    public Protocol<List<TreeNodeVo>> children(@ApiParam(value = "目录ID", required = true)
                                               @RequestParam(value = "folderId", required = true) Long folderId) {
        List<TreeNode> children = treeService.findChildren(folderId);
        return new Protocol<>(children.stream().map(TreeNodeVo::fromTreeNode).collect(Collectors.toList()));
    }

    @ApiOperation(value = "创建目录节点", notes = "创建目录节点")
    @RequestMapping(value = "/addFolder", method = RequestMethod.POST)
    public Protocol<TreeNodeVo> addFolder(@ApiParam(value = "父亲节点ID", required = true)
                                          @RequestParam(value = "parentId", required = true) Long parentId,
                                          @ApiParam(value = "目录名", required = true)
                                          @RequestParam(value = "folderName", required = true) String folderName) {
        TreeNode node = treeService.addFolderNode(folderName, parentId);
        return new Protocol<>(TreeNodeVo.fromTreeNode(node));
    }

    @ApiOperation(value = "重新命名目录", notes = "重新命名目录")
    @RequestMapping(value = "/renameFolder", method = RequestMethod.POST)
    public Protocol<Boolean> renameFolder(@ApiParam(value = "目录节点ID", required = true)
                                          @RequestParam(value = "folderId", required = true) Long folderId,
                                          @ApiParam(value = "新名字", required = true)
                                          @RequestParam(value = "newName", required = true) String newName) {
        Boolean result = treeService.rename(folderId, newName);
        return new Protocol<>(result);
    }

    @ApiOperation(value = "删除目录", notes = "删除目录")
    @RequestMapping(value = "/deleteFolder", method = RequestMethod.POST)
    public Protocol<Boolean> deleteFolder(@ApiParam(value = "目录ID", required = true)
                                          @RequestParam(value = "folderId", required = true) Long folderId) {
        Boolean result = treeService.deleteFolder(folderId);
        return new Protocol<>(result);
    }

    @ApiOperation(value = "数据库列表", notes = "数据库列表")
    @RequestMapping(value = "/databases", method = RequestMethod.GET)
    public Protocol<List<TreeNodeVo>> databases() {
        List<TreeNode> databases = treeService.findAllDbNodes();
        return new Protocol<>(databases.stream().map(TreeNodeVo::fromTreeNode).collect(Collectors.toList()));
    }

    @ApiOperation(value = "表详细信息", notes = "返回表详细信息，包括表说明和表字段说明")
    @RequestMapping(value = "/tableDetail", method = RequestMethod.GET)
    public Protocol<TableDetail> tableDetail(@ApiParam(value = "表节点ID", required = true)
                                             @RequestParam(value = "tableId", required = true) Long tableId) {
        TableDetail tableDetail = treeService.tableDetail(tableId);
        return new Protocol<>(tableDetail);
    }

    @ApiOperation(value = "移动至某目录", notes = "将节点移动至某目录下")
    @RequestMapping(value = "/moveTo", method = RequestMethod.POST)
    public Protocol<Boolean> moveTo(@ApiParam(value = "当前节点ID列表, 英文逗号分隔", required = true)
                                    @RequestParam(value = "nodeIdList", required = true) String nodeIdList,
                                    @ApiParam(value = "目标目录ID", required = true)
                                    @RequestParam(value = "parentId", required = true) Long parentId) {
        List<Long> idList = Splitter.on(',').splitToList(nodeIdList).stream()
                .map(Long::parseLong).collect(Collectors.toList());
        Boolean result = treeService.moveNodeTo(idList, parentId);
        return new Protocol<>(result);
    }

    @ApiOperation(value = "目录的表分类信息综合", notes = "获取目录表分类信息综合信息")
    @RequestMapping(value = "/folderClassifyInfo", method = RequestMethod.GET)
    public Protocol<FolderClassifyInfo> folderClassifyInfo(@ApiParam(value = "目录ID", required = true)
                                                           @RequestParam(value = "folderId", required = true) Long folderId) {
        return new Protocol<>(treeService.folderClassifyInfo(folderId));
    }

    @ApiOperation(value = "更新列注释", notes = "更新列注释")
    @RequestMapping(value = "/updateColumnComment", method = RequestMethod.POST)
    public Protocol<Boolean> updateColumnComment(@ApiParam(value = "表节点ID", required = true)
                                                 @RequestParam(value = "nodeId", required = true) Long nodeId,
                                                 @ApiParam(value = "列名", required = true)
                                                 @RequestParam(value = "columnName", required = true) String columnName,
                                                 @ApiParam(value = "新注释", required = true)
                                                 @RequestParam(value = "newComment", required = true) String newComment) {
        return new Protocol<>(treeService.updateColumnComment(nodeId, columnName, newComment));
    }

    @ApiOperation(value = "更新表注释", notes = "更新表注释")
    @RequestMapping(value = "/updateTableComment", method = RequestMethod.POST)
    public Protocol<Boolean> updateTableComment(@ApiParam(value = "数据库", required = true) @RequestParam(value = "dbName", required = true) String dbName,
                                                @ApiParam(value = "表名", required = true) @RequestParam(value = "tableName", required = true) String tableName,
                                                @ApiParam(value = "新注释", required = true) @RequestParam(value = "newComment", required = true) String newComment) {
        return new Protocol<>(treeService.updateTableComment(dbName, tableName, newComment));
    }
}
