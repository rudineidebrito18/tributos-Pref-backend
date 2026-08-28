package br.com.tributos.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Substitui o {@code DataSource} auto-configurado pelo Spring Boot por
 * {@link TenantAwareDataSource}. Definir este {@code @Bean} explicitamente faz a
 * autoconfiguração padrão do Hikari recuar (ela só se aplica com
 * {@code @ConditionalOnMissingBean(DataSource.class)}); {@link DataSourceProperties}
 * continua disponível para reaproveitar toda a configuração de
 * {@code spring.datasource.*} normalmente.
 */
@Configuration
public class TenantDataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource hikariDataSource = properties.initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
        return new TenantAwareDataSource(hikariDataSource);
    }
}
