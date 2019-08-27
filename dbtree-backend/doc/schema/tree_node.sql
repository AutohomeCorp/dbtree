CREATE TABLE IF NOT EXISTS tree_node
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    db_name        VARCHAR(100) NOT NULL COMMENT '数据库名',
    folder         INTEGER      NOT NULL DEFAULT 0 COMMENT '0：表节点; 1: 目录节点',
    node_name      VARCHAR(200) NOT NULL COMMENT '表名',
    parent_id      BIGINT       NOT NULL DEFAULT 0 COMMENT '父亲节点id',
    created_stime  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    modified_stime DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_del         INTEGER      NOT NULL DEFAULT 0 COMMENT '逻辑删除标识'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '树形数据表';