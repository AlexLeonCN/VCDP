-- 初始化数据脚本
-- 注意：H2 数据库使用 MERGE 语句来避免重复插入

-- 插入默认管理员用户（密码：123456）
-- 暂时使用明文密码，后续可以添加 BCrypt 加密
MERGE INTO `user` (username, password, nickname, email, status) KEY(username)
VALUES ('admin', '123456', '管理员', 'admin@vcdp.com', 1);

-- 插入测试用户（密码：test123）
MERGE INTO `user` (username, password, nickname, email, status) KEY(username)
VALUES ('test', 'test123', '测试用户', 'test@vcdp.com', 1);

