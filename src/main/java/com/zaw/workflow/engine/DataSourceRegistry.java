package com.zaw.workflow.engine;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源注册表（database -> JdbcTemplate）
 */
@Component
public class DataSourceRegistry {

    private final Map<String, NamedParameterJdbcTemplate> registry = new ConcurrentHashMap<>();

    public void register(String database, DataSource dataSource) {
        registry.put(database, new NamedParameterJdbcTemplate(dataSource));
    }

    public NamedParameterJdbcTemplate get(String database) {
        NamedParameterJdbcTemplate tpl = registry.get(database);
        if (tpl == null) {
            throw new IllegalArgumentException("Unknown database: " + database);
        }
        return tpl;
    }
}
