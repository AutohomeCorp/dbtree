package com.autohome.dbtree.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class FolderClassifyInfo {

    @ApiModelProperty(value = "所有表(包括未分类的表，和本目录下的下一层子表)")
    private List<TreeNodeVo> tables;

    @ApiModelProperty(value = "库里未分类表id列表")
    private List<Long> notClassifiedIdList;

    public List<TreeNodeVo> getTables() {
        return tables;
    }

    public void setTables(List<TreeNodeVo> tables) {
        this.tables = tables;
    }

    public List<Long> getNotClassifiedIdList() {
        return notClassifiedIdList;
    }

    public void setNotClassifiedIdList(List<Long> notClassifiedIdList) {
        this.notClassifiedIdList = notClassifiedIdList;
    }
}
