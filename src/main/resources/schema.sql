-- 数据库表结构初始化脚本

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    `nickname` VARCHAR(100) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 创建索引
CREATE INDEX IF NOT EXISTS `idx_username` ON `user`(`username`);
CREATE INDEX IF NOT EXISTS `idx_status` ON `user`(`status`);

