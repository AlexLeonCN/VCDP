-- 初始化数据脚本
-- 注意：H2 数据库使用 MERGE 语句来避免重复插入

-- 插入默认管理员用户（密码：123456）
-- 暂时使用明文密码，后续可以添加 BCrypt 加密
MERGE INTO `user` (username, password, nickname, email, status) KEY(username)
VALUES ('admin', '123456', '管理员', 'alex.leon@aliyun.com', 1);

-- 插入测试用户（密码：test123）
MERGE INTO `user` (username, password, nickname, email, status) KEY(username)
VALUES ('test', 'test123', '测试用户', 'test@vcdp.com', 1);

-- 插入角色数据
MERGE INTO `role` (code, name, description, status) KEY(code)
VALUES ('admin', '管理员', '系统管理员，拥有所有权限', 1);

MERGE INTO `role` (code, name, description, status) KEY(code)
VALUES ('user', '普通用户', '普通用户，拥有基本权限', 1);

-- 插入权限数据
MERGE INTO `permission` (code, name, resource, action, description, status) KEY(code)
VALUES ('user:create', '创建用户', 'user', 'create', '创建用户权限', 1);

MERGE INTO `permission` (code, name, resource, action, description, status) KEY(code)
VALUES ('user:read', '查看用户', 'user', 'read', '查看用户权限', 1);

MERGE INTO `permission` (code, name, resource, action, description, status) KEY(code)
VALUES ('user:update', '更新用户', 'user', 'update', '更新用户权限', 1);

MERGE INTO `permission` (code, name, resource, action, description, status) KEY(code)
VALUES ('user:delete', '删除用户', 'user', 'delete', '删除用户权限', 1);

MERGE INTO `permission` (code, name, resource, action, description, status) KEY(code)
VALUES ('system:config', '系统配置', 'system', 'config', '系统配置权限', 1);

-- 分配用户角色（admin 用户 -> admin 角色）
MERGE INTO `user_role` (user_id, role_id) KEY(user_id, role_id)
SELECT u.id, r.id FROM `user` u, `role` r WHERE u.username = 'admin' AND r.code = 'admin';

-- 分配用户角色（test 用户 -> user 角色）
MERGE INTO `user_role` (user_id, role_id) KEY(user_id, role_id)
SELECT u.id, r.id FROM `user` u, `role` r WHERE u.username = 'test' AND r.code = 'user';

-- 分配角色权限（admin 角色拥有所有权限）
MERGE INTO `role_permission` (role_id, permission_id) KEY(role_id, permission_id)
SELECT r.id, p.id FROM `role` r, `permission` p WHERE r.code = 'admin';

-- 分配角色权限（user 角色拥有基本权限）
MERGE INTO `role_permission` (role_id, permission_id) KEY(role_id, permission_id)
SELECT r.id, p.id FROM `role` r, `permission` p WHERE r.code = 'user' AND p.code IN ('user:read');

