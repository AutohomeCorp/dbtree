<template>
  <div class="app-container">
    <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />
    <el-row :gutter="20">
        <el-col :span="5">
            <div class="custom-tree-container" style="overflow-y:auto; height: 900px">
              <div class="block">
                <el-tree
                ref="tree"
                :data="data2"
                :props="props"
                node-key="id"
                :indent="16"
                :accordion="true"
                lazy
                :load="loadNode"
                :draggable="true"
                :highlight-current="true"
                :expand-on-click-node="true"
                :allow-drop="allowDrop"
                :filter-node-method="filterNode"
                @node-click="clickNode"
                @node-drop="dropNodeHandle">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                  <i v-if="!data.leaf" class="el-icon-message" /><i v-if="data.leaf" class="el-icon-document" />
                  <span class="tree-label" v-if="data.edit" @click.stop="">
                    <el-input size="mini" v-model="data.node_name" ref="treeInput"
                    @blur.stop="() => blur(node, data)" @keyup.native="() => editKeyUp(data, $event)"></el-input>
                  </span>
                  <template v-else>
                    <span>{{ node.label }}</span>
                    <span v-if="!data.leaf">
                        <i class="el-icon-circle-plus" @click.stop="() => append(data)" />
                        <i class="el-icon-edit" v-if="data.parent_id > 0" @click.stop="() => edit(data)" />
                        <i class="el-icon-tickets" v-if="data.parent_id > 0" @click.stop="() => classify(data)" />
                        <i class="el-icon-delete" v-if="data.parent_id > 0" @click.stop="() => remove(node, data)" />
                    </span>
                  </template>
                </span>
                </el-tree>
              </div>
            </div>
        </el-col>
        <el-col :span="19">

          <div :class="className" 
            :style="{top:(isSticky ? stickyTop +'px' : ''),zIndex:zIndex,position:position,width:width,height:height+'px'}">
            <slot>
              <div style="text-align: center; padding-bottom: 20px" v-if="tableData.dataType=='column'">
                <span>{{ tableData.info.tableName }} :  {{ tableData.info.description }}</span>
              </div>
              <el-table v-if="tableData.dataType=='column'"
                :data="tableData.list"
                element-loading-text="Loading"
                border
                fit
                highlight-current-row>
                <el-table-column align="center" label="序号" width="95">
                  <template slot-scope="scope">
                    {{ scope.$index }}
                  </template>
                </el-table-column>
                <el-table-column align="center" label="字段名" width="220">
                  <template slot-scope="scope">
                    {{ scope.row.columnName }}
                  </template>
                </el-table-column>
                <el-table-column label="类型" align="center" width="150">
                  <template slot-scope="scope">
                    <span>{{ scope.row.columnType }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="自增" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.autoIncrement }}
                  </template>
                </el-table-column>
                <el-table-column label="可空" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.nullable }}
                  </template>
                </el-table-column>
                <el-table-column label="主键" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.primaryKey }}
                  </template>
                </el-table-column>
                <el-table-column label="默认值" align="center">
                  <template slot-scope="scope">
                    {{ scope.row.columnDefault }}
                  </template>
                </el-table-column>
                <el-table-column label="注释" align="center" width="400">
                  <template slot-scope="{row}">
                    <template v-if="row.edit">
                      <el-input type="textarea" v-model="row.columnComment" class="edit-input" size="small" />
                      <el-button
                        class="cancel-btn"
                        size="small"
                        icon="el-icon-refresh"
                        type="warning"
                        @click="cancelColumnEdit(row)"
                      >
                        取消
                      </el-button>
                    </template>
                    <span v-else>{{ row.columnComment }}</span>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="修改注释" width="120">
                  <template slot-scope="{row}">
                    <el-button
                      v-if="row.edit"
                      type="success"
                      size="small"
                      icon="el-icon-circle-check-outline"
                      @click="confirmColumnEdit(row)"
                    >
                      确定
                    </el-button>
                    <el-button
                      v-else
                      type="primary"
                      size="small"
                      icon="el-icon-edit"
                      @click="row.edit=!row.edit"
                    >
                      编辑
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <el-table v-if="tableData.dataType=='table'"
                :data="tableData.list"
                v-loading="listLoading"
                element-loading-text="Loading"
                border
                fit
                highlight-current-row>
                <el-table-column align="center" label="序号" width="95">
                  <template slot-scope="scope">
                    {{ scope.$index }}
                  </template>
                </el-table-column>
                <el-table-column align="center" label="表名">
                  <template slot-scope="scope">
                    {{ scope.row.tableName }}
                  </template>
                </el-table-column>
                <el-table-column align="center" label="表说明">
                  <template slot-scope="{row}">
                    <template v-if="row.edit">
                      <el-input v-model="row.description" class="edit-input" size="small" />
                      <el-button
                        class="cancel-btn"
                        size="small"
                        icon="el-icon-refresh"
                        type="warning"
                        @click="cancelTableEdit(row)"
                      >
                        取消
                      </el-button>
                    </template>
                    <span v-else>{{ row.description }}</span>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="修改注释" width="120">
                  <template slot-scope="{row}">
                    <el-button
                      v-if="row.edit"
                      type="success"
                      size="small"
                      icon="el-icon-circle-check-outline"
                      @click="confirmTableEdit(row)"
                    >
                      确定
                    </el-button>
                    <el-button
                      v-else
                      type="primary"
                      size="small"
                      icon="el-icon-edit"
                      @click="row.edit=!row.edit"
                    >
                      编辑
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </slot>
          </div>
          
        </el-col>
    </el-row>

    <el-dialog :title="classifyDialogTitle" :visible.sync="dialogVisible" width="50%">
      <el-transfer
        filterable
        :titles="classifyData.titles"
        :filter-method="filterClassifyMethod"
        filter-placeholder="请输入表名"
        v-model="classifyData.value"
        :props="classifyData.props"
        :data="classifyData.data"
        @change="handleChange">
      </el-transfer>
      <!-- <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
      </div> -->
    </el-dialog>
  </div>
</template>>

<script>
  import dbtree from '@/api/dbtree.js'
  let id = -1;

  export default {
    props: {
      stickyTop: {
        type: Number,
        default: 0
      },
      zIndex: {
        type: Number,
        default: 1
      },
      className: {
        type: String,
        default: ''
      }
    },
    data() {
      const generateData = _ => {
        const data = [];
        for (let i = 1; i <= 15; i++) {
          data.push({
            id: i,
            node_name: `table ${ i }`
          });
        }
        return data;
      };

      return {
        active: false,
        position: '',
        width: undefined,
        height: undefined,
        isSticky: false,
        props: {
          label: 'node_name',
          children: 'children',
          isLeaf: 'leaf'
        },
        data2: [],
        filterText: '',
        listLoading: true,
        tableData: {
          dataType: 'column',
          list: [],
          info: {
            tableName: 'name',
            description: 'desc'
          }
        },
        classifyDialogTitle: '表分类',
        dialogVisible: false,
        classifyData: {
          folderId: 0,
          titles: ["已在目录表", "未分类表"],
          props: {
            key: 'id', 
            label: 'node_name'
          },
          data: [],
          value: []
        }
      };
    },

    mounted() {
    },

    activated() {
    },

    destroyed() {
    },

    watch: {
      filterText(val) {
        this.$refs.tree.filter(val)
      }
    },

    methods: {
      append(data) {
        var defaultName = 'node' + (id--);
        dbtree.addFolder(data.id, defaultName).then(response =>{
          var newChild = {
            id: response.data.result.id,
            node_name:  response.data.result.node_name,
            edit : false,
            parent_id: response.data.result.parent_id,
            leaf: response.data.result.leaf,
            database: response.data.result.database
          };
          if (!data.children) {
            this.$set(data, 'children', []);
          }
          data.children.push(newChild);
        });
      },

      remove(node, data) {
        if(data.parent_id === 0) {
          return;
        }
        dbtree.deleteFolder(data.id).then(response => {
          if(!response.data.result) {
            this.$message({ 
              showClose: true,
              message: '包含子节点目录无法删除！',
              type: 'error'
            });
            return;
          }
          const parent = node.parent;
          const children = parent.childNodes;
          const index = children.findIndex(d => d.data.id === data.id);
          children.splice(index, 1);
        });
      },

      edit(data) {
        data.edit = true;
        setTimeout( () => {
          this.$refs.treeInput.$el.querySelector('input').focus();
        }, 1);
        
      },

      classify(data) {
        this.dialogVisible = true;
        this.classifyData.folderId = data.id;
        this.classifyDialogTitle = '目录: ' + data.node_name;
        dbtree.folderClassifyInfo(data.id).then(response => {
          this.classifyData.data = response.data.result.tables;
          this.classifyData.value = response.data.result.notClassifiedIdList;
        });
      },

      blur(node, data) {
        data.edit = false;
        dbtree.renameFolder(data.id, data.node_name).then(response => {
          if(!response.data.result) {
            this.$message({
              showClose: true,
              message: '目录改名失败！',
              type: 'error'
            });
          }
        });
      },

      editKeyUp(data, event) {
        if(event.keyCode === 13) {
          data.edit = false;
          dbtree.renameFolder(data.id, data.node_name).then(response => {
          if(!response.data.result) {
            this.$message({
              showClose: true,
              message: '目录改名失败！',
              type: 'error'
            });
          }
        });
        }
      },

      loadNode(node, resolve) {
        if(node.level === 0) {
          dbtree.databases().then(response => {
            resolve(response.data.result);
          });
          
          return;
        }

        dbtree.children(node.data.id).then(response => {
          setTimeout(() => {
            resolve(response.data.result);
          }, 100);
        });
      },

      allowDrop(draggingNode, dropNode, type) {
        if(type === 'inner' && dropNode.data.leaf) {
          return false;
        }

        if(draggingNode.data.database != dropNode.data.database) {
          return false;
        }

        if(type == 'inner' && draggingNode.data.parent_id === dropNode.data.id) {
          return false;
        }

        return true;
      },

      clickNode(data, node, ele) {
        this.listLoading = true;
        if(data.leaf) {
          this.$data.tableData.dataType = "column";
          dbtree.tableDetail(data.id).then(response => {
            this.tableData.list = response.data.result.columnInfoList.map(v => {
              this.$set(v, 'edit', false);
              v.originalColumnComment = v.columnComment;
              return v;
            });
            this.tableData.info = response.data.result.tableInfo;
            this.listLoading = false;
          });
        } else{
          this.$data.tableData.dataType = "table";
          dbtree.tableList(data.id).then(response => {
            this.$data.tableData.list = response.data.result.map(v => {
              this.$set(v, 'edit', false);
              v.originalDescription = v.description;
              return v;
            });
            this.listLoading = false;
          });
        }
      },

      filterNode(value, data) {
        if (!value) return true
        return data.node_name.toLowerCase().indexOf(value.toLowerCase()) !== -1
      },

      handleChange(value, direction, movedKeys) {
        var folderNode = this.$refs.tree.getNode(this.classifyData.folderId);
        if(direction === 'left') {
          dbtree.moveTo(movedKeys, this.classifyData.folderId).then(response => {
            for(var i = 0; i < movedKeys.length; i++) {
              var movedNode = this.$refs.tree.getNode(movedKeys[i]);
              var data = movedNode.data;
              this.$refs.tree.remove(movedNode);
              this.$refs.tree.append(data, folderNode);
            }
          });
        }

        if(direction === 'right') {
          dbtree.databases().then(response => {
            let dbId = 0;
            for(var i = 0; i < response.data.result.length; i++) {
              if(response.data.result[i].node_name == folderNode.data.database) {
                dbId = response.data.result[i].id;
                break;
              }
            }
            if(dbId === 0) {
              return;
            }

            var dbNode = this.$refs.tree.getNode(dbId);

            dbtree.moveTo(movedKeys, dbId).then(response => {
              for(var i = 0; i < movedKeys.length; i++) {
                var movedNode = this.$refs.tree.getNode(movedKeys[i]);
                var data = movedNode.data;
                this.$refs.tree.remove(movedNode);
                this.$refs.tree.append(data, dbNode);
              }
            });
          });
        }
      },

      dropNodeHandle(draggingNode, dropNode, type, event) {
        let parentId = 0;
        if(type === 'inner') {
          parentId = dropNode.data.id;
        } else {
          parentId = dropNode.parent.data.id;
        }

        if(parentId == null) {
          return;
        }

        dbtree.moveTo([draggingNode.data.id], parentId).then(response => {
            if(!response.data.result) {
              this.$message({
                showClose: true,
                message: '移动失败',
                type: 'error'
              });
            }
          });
      },

      filterClassifyMethod(query, item) {
        return item.node_name.toLowerCase().indexOf(query.toLowerCase()) > -1
      },

      cancelColumnEdit(row) {
        row.columnComment = row.originalColumnComment;
        row.edit = false;
        this.$message({
          message: '修改取消',
          type: 'warning'
        });
      },

      confirmColumnEdit(row) {
        var nodeId = this.$refs.tree.getCurrentKey();
        dbtree.updateColumnComment(nodeId, row.columnName, row.columnComment).then(response => {
          if(!response.data.result) {
            row.columnComment = row.originalColumnComment;
            row.edit = false;
            this.$message({
              message: '修改失败',
              type: 'warning'
            });
          } else {
            row.edit = false;
            row.originalColumnComment = row.columnComment;
            this.$message({
              message: '修改成功',
              type: 'success'
            });
          }
        });
      },

      cancelTableEdit(row) {
        row.description = row.originalDescription;
        row.edit = false;
        this.$message({
          message: '修改取消',
          type: 'warning'
        });
      },

      confirmTableEdit(row) {
        var dbName = this.$refs.tree.getCurrentNode().database;
        dbtree.updateTableComment(dbName, row.tableName, row.description).then(response => {
          if(response.data.result) {
            row.edit = false;
            row.originalDescription = row.description;
            this.$message({
              message: '修改成功',
              type: 'success'
            });
          } else {
            row.description = row.originalDescription;
            row.edit = false;
            this.$message({
              message: '修改失败',
              type: 'warning'
            });
          }
        });
        
      },

      sticky() {
        if (this.active) {
          return
        }
        this.position = 'fixed'
        this.active = true
        this.width = this.width + 'px'
        this.isSticky = true
      },

      handleReset() {
      },

      reset() {
        this.position = ''
        this.width = 'auto'
        this.active = false
        this.isSticky = false
      },

      handleScroll() {
      
      },

      handleResize() {
      }
    }
  };
</script>

<style>
  .custom-tree-node {
    /* flex: 1;
    display: flex; */
    align-items: center;
    justify-content: space-between;
    font-size: 15px;
    padding-right: 100px;
  }

  /* .el-table th, .el-table tr {
    border-right: 1px solid #D7D7E5;
    border-bottom: 1px solid #D7D7E5;
    background-color: #F0F0F0;
  }

  .el-table--enable-row-transition .el-table__body td {
    border-right: 1px solid #D7D7E5;
    border-bottom: 1px solid #D7D7E5; 
    background-color:#FBFBFB
  } */

  .el-transfer-panel {
    width: 400px;
  }

  .edit-input {
    padding-right: 100px;
  }

  .cancel-btn {
    position: absolute;
    right: 15px;
    top: 10px;
  }
</style>