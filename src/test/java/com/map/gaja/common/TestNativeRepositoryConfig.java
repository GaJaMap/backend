package com.map.gaja.common;

import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.querydsl.sql.NativeSqlCreator;
import com.map.gaja.client.infrastructure.repository.querydsl.sql.PostgreSQLNativeSqlCreator;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

@TestConfiguration
public class TestNativeRepositoryConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public NativeSqlCreator nativeSqlCreator() {
        return new PostgreSQLNativeSqlCreator();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ClientQueryRepository clientQueryRepository() {
        return new ClientQueryRepository(jpaQueryFactory(), nativeSqlCreator());
    }

    @Bean
    public ClientBulkRepository clientBulkRepository() {
        return new ClientBulkRepository(jdbcTemplate());
    }
}