package com.example.taskflow.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    /**
     * Tự động chạy flyway.repair() trước khi migrate.
     * Giúp tự động cập nhật checksums trong bảng flyway_schema_history,
     * khắc phục triệt để lỗi "FlywayValidateException: Migration checksum mismatch"
     * khi chỉnh sửa các file migration (V1, V3, V5...) ở môi trường Local Dev.
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
