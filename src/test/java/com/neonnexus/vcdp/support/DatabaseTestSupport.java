package com.neonnexus.vcdp.support;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * 测试数据库初始化工具：清除并重建 H2 文件，再基于 schema.sql 建表。
 */
public final class DatabaseTestSupport {
    private DatabaseTestSupport() {
    }

    /**
     * 清除并重新创建 H2 的 .mv 文件，并基于 schema.sql 重新创建数据库表格。
     */
    public static void initDatabase(DataSource dataSource, JdbcTemplate jdbcTemplate) throws IOException {
        jdbcTemplate.execute("DROP ALL OBJECTS DELETE FILES");
        deleteDatabaseFiles(Paths.get("./data"), "vcdp");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.setContinueOnError(false);
        populator.execute(dataSource);
    }

    public static void deleteDatabaseFiles(Path dataDir, String dbName) throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
            return;
        }
        try (Stream<Path> stream = Files.list(dataDir)) {
            stream.filter(path -> {
                        String name = path.getFileName().toString();
                        return name.equals(dbName + ".mv.db")
                                || name.equals(dbName + ".trace.db")
                                || name.equals(dbName + ".lock.db");
                    })
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                            // ignore delete failure while file may be reopened by datasource
                        }
                    });
        }
    }
}
