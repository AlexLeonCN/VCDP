-- 初始化数据脚本
-- 注意：H2 数据库使用 MERGE 语句来避免重复插入

-- 插入默认管理员用户（密码：alex940712，已使用 SHA-256 + 盐值加密）
MERGE INTO `user` (user_name, password, nick_name, email, status) KEY(user_name)
VALUES ('admin', 'YWRtaW5TYWx0MjAyNFZDRFA=:8liNtsD3wwTni7uYjOOiC5tPr8CYYzsv8nDX7K+IkOc=', '管理员', 'alex.leon@aliyun.com', 1);

-- 插入测试用户（密码：test1234，已使用 SHA-256 + 盐值加密）
MERGE INTO `user` (user_name, password, nick_name, email, status) KEY(user_name)
VALUES ('test', 'dGVzdFNhbHQyMDI0VkNEUDEy:LQ+E/0I/6rfs2N9nt5TLeelohxo8P0q6ZsUWvnoghTc=', '测试用户', 'test@vcdp.com', 1);

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
SELECT u.id, r.id FROM `user` u, `role` r WHERE u.user_name = 'admin' AND r.code = 'admin';

-- 分配用户角色（test 用户 -> user 角色）
MERGE INTO `user_role` (user_id, role_id) KEY(user_id, role_id)
SELECT u.id, r.id FROM `user` u, `role` r WHERE u.user_name = 'test' AND r.code = 'user';

-- 分配角色权限（admin 角色拥有所有权限）
MERGE INTO `role_permission` (role_id, permission_id) KEY(role_id, permission_id)
SELECT r.id, p.id FROM `role` r, `permission` p WHERE r.code = 'admin';

-- 分配角色权限（user 角色拥有基本权限）
MERGE INTO `role_permission` (role_id, permission_id) KEY(role_id, permission_id)
SELECT r.id, p.id FROM `role` r, `permission` p WHERE r.code = 'user' AND p.code IN ('user:read');

