<template>
    <el-row>
        <el-col :span="6">
            <div class="custom-tree-container" style="overflow-y:auto; height: 980px">
              <div class="block">
                <el-tree
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
                @node-click="clickNode"
                @node-drop="dropNodeHandle">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                  <i v-if="!data.leaf" class="el-icon-message" /><i v-if="data.leaf" class="el-icon-document" />
                  <span class="tree-label" v-if="data.edit" @click.stop="">
                    <el-input size="mini" v-model="data.node_name" ref="treeInput"
                    v-focus="data.edit"
                    @blur.stop="() => blur(node, data)" @keyup.native="() => editKeyUp(data, $event)"></el-input>
                  </span>
                  <template v-else>
                    <span>{{ node.label }}</span>
                    <span v-if="!data.leaf">
                        <i class="el-icon-circle-plus" @click.stop="() => append(data)" />
                        <i class="el-icon-edit" v-if="data.parent_id > 0" @click.stop="() => edit(data)" />
                        <i class="el-icon-delete" v-if="data.parent_id > 0" @click.stop="() => remove(node, data)" />
                    </span>
                  </template>
                </span>
                </el-tree>
              </div>
            </div>
        </el-col>
        <el-col :span="18">

          <div :class="className" 
            :style="{top:(isSticky ? stickyTop +'px' : ''),zIndex:zIndex,position:position,width:width,height:height+'px'}">
            <slot>
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
                  <template slot-scope="scope">
                    <span>{{ scope.row.columnComment }}</span>
                  </template>
                </el-table-column>
              </el-table>

              <div v-if="tableData.dataType=='table'">
                <el-button v-waves :loading="downloadLoading" class="filter-item" type="primary" @click="handleDownload">生成Mybatis资源</el-button>
                <a href="/code/mybatisDownload?zipFile=atom_20190906-182447.zip">点击下载</a>
                <el-table
                  :data="tableData.list"
                  v-loading="listLoading"
                  element-loading-text="Loading"
                  border
                  fit
                  highlight-current-row>
                  <el-table-column type="selection" width="55"></el-table-column>
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
                    <template slot-scope="scope">
                      {{ scope.row.description }}
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </slot>
          </div>
          
        </el-col>
    </el-row>
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
        listLoading: true,
        tableData: {
          dataType: 'column',
          list: []
        }
      };
    },

    mounted() {
    },

    activated() {
    },

    destroyed() {
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
            this.$data.tableData.list = response.data.result.columnInfoList;
            this.listLoading = false;
          });
        } else{
          this.$data.tableData.dataType = "table";
          dbtree.tableList(data.id).then(response => {
            this.$data.tableData.list = response.data.result;
            this.listLoading = false;
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

        dbtree.moveTo(draggingNode.data.id, parentId).then(response => {
            if(!response.data.result) {
              this.$message({ 
                showClose: true,
                message: '移动失败',
                type: 'error'
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
    font-size: 14px;
    padding-right: 100px;
  }
</style>