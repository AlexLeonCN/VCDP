-- 本地单机工具数据库表结构初始化脚本

CREATE TABLE IF NOT EXISTS project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_project_update_time ON project(update_time);

CREATE TABLE IF NOT EXISTS ecu (
    id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    ecu_type VARCHAR(100),
    description VARCHAR(500),
    mac VARCHAR(12) NOT NULL,
    ip VARCHAR(45) NOT NULL,
    port INTEGER NOT NULL,
    ecu_index INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ecu_project_id ON ecu(project_id);
CREATE INDEX IF NOT EXISTS idx_ecu_update_time ON ecu(update_time);

CREATE TABLE IF NOT EXISTS ecu_forward_info (
    id BIGINT PRIMARY KEY,
    ecu_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    p_flash_memory_start_address VARCHAR(32) NOT NULL,
    p_flash_memory_size_limit VARCHAR(32) NOT NULL,
    ram_memory_start_address VARCHAR(32) NOT NULL,
    ram_memory_size_limit VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ecu_forward_ecu_id ON ecu_forward_info(ecu_id);

CREATE TABLE IF NOT EXISTS ecu_can_interface (
    id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    ecu_id BIGINT NOT NULL,
    interface_name VARCHAR(100) NOT NULL,
    channel_id INTEGER NOT NULL,
    interface_type TINYINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ecu_can_ecu_id ON ecu_can_interface(ecu_id);

CREATE TABLE IF NOT EXISTS ecu_lin_interface (
    id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    ecu_id BIGINT NOT NULL,
    interface_name VARCHAR(100) NOT NULL,
    channel_id INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ecu_lin_ecu_id ON ecu_lin_interface(ecu_id);

CREATE TABLE IF NOT EXISTS ecu_eth_interface (
    id BIGINT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    ecu_id BIGINT NOT NULL,
    interface_name VARCHAR(100) NOT NULL,
    port_type TINYINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ecu_eth_ecu_id ON ecu_eth_interface(ecu_id);
