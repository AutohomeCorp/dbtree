import request from '@/utils/request'

const dbtree = {
    /**
     * 查询目录下的表
     * @param {*} folderId 目录ID
     */
    tableList ( folderId ) {
        return request({
            url: '/tree/tableList',
            method: 'get',
            params: { folderId: folderId }
        })
    },

    /**
     * 查询目录下所有节点
     * @param {*} folderId 目录ID
     */
    children (folderId) {
        return request({
            url: '/tree/children',
            method: 'get',
            params: { folderId: folderId }
        })
    },

    /**
     * 创建新目录
     * @param {*} parentId 父亲节点ID
     * @param {*} folderName 目录名
     */
    addFolder (parentId, folderName) {
        return request({
            url: '/tree/addFolder',
            method: 'post',
            params: {
                parentId: parentId,
                folderName: folderName
            }
        })
    },

    /**
     * 重新命名目录
     * @param {*} folderId 目录ID
     * @param {*} newName 新名字
     */
    renameFolder (folderId, newName) {
        return request({
            url: '/tree/renameFolder',
            method: 'post',
            params: { folderId: folderId, newName: newName }
        })
    },



    /**
     * 数据库列表
     */
    databases() {
        return request({
            url: '/tree/databases',
            method: 'get'
        })
    },

    /**
     * 删除目录
     * @param {*} folderId 待删除目录ID
     */
    deleteFolder(folderId) {
        return request({
            url: '/tree/deleteFolder',
            method: 'post',
            params: {
                folderId: folderId
            }
        })
    },

    /**
     * 表详细信息
     * @param {*} tableId 表节点ID 
     */
    tableDetail(tableId) {
        return request({
            url: '/tree/tableDetail',
            method: 'get',
            params: {
                tableId: tableId
            }
        })
    },

    /**
     * 移动节点到某目录下
     * @param {*} nodeId 当前节点ID
     * @param {*} parentId 目录目录ID
     */
    moveTo(nodeIdList, parentId) {
      return request({
        url: '/tree/moveTo',
        method: 'post',
        params: {
          nodeIdList: nodeIdList.join(','),
          parentId: parentId
        }
      });
    },

    /**
     * 获取目录综合分类信息
     * @param {*} folderId 目录ID
     */
    folderClassifyInfo(folderId) {
      return request({
        url: '/tree/folderClassifyInfo',
        method: 'get',
        params: {
          folderId: folderId
        }
      });
    },

    /**
     * 更新列注释
     * @param {*} nodeId 表节点ID
     * @param {*} columnName 列名
     * @param {*} newComment 新注释
     */
    updateColumnComment(nodeId, columnName, newComment) {
      return request({
        url: '/tree/updateColumnComment',
        method: 'post',
        params: {
          nodeId: nodeId,
          columnName: columnName,
          newComment: newComment
        }
      });
    },

    /**
     * 修改表注释
     * @param {*} dbName 数据库
     * @param {*} tableName 表名
     * @param {*} newComment 新注释
     */
    updateTableComment(dbName, tableName, newComment) {
      return request({
        url: '/tree/updateTableComment',
        method: 'post',
        params: {
          dbName: dbName,
          tableName: tableName,
          newComment: newComment
        }
      });
    }
}

export default dbtree