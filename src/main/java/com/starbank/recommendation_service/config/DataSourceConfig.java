package com.starbank.recommendation_service.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.starbank.recommendation_service.dynamic.repository",
        entityManagerFactoryRef = "rulesEntityManagerFactory",
        transactionManagerRef = "rulesTransactionManager"
)
public class DataSourceConfig {

    // === H2: дефолтный (@Primary) ===
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties h2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource h2DataSource(@Qualifier("h2DataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource h2) {
        return new JdbcTemplate(h2);
    }

    // === Postgres: rules ===
    @Bean
    @ConfigurationProperties("rules.datasource")
    public DataSourceProperties rulesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "rulesDataSource")
    public DataSource rulesDataSource(@Qualifier("rulesDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "rulesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean rulesEmf(
            @Qualifier("rulesDataSource") DataSource ds) {
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setPackagesToScan("com.starbank.recommendation_service.dynamic.model");
        em.setPersistenceUnitName("rules");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(Map.of(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.hbm2ddl.auto", "none"
        ));
        return em;
    }

    @Bean(name = "rulesTransactionManager")
    public PlatformTransactionManager rulesTx(
            @Qualifier("rulesEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    // Liquibase для второй базы данных
    @Bean
    @ConfigurationProperties("rules.liquibase")
    public SpringLiquibase rulesLiquibase(@Qualifier("rulesDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setDefaultSchema("public");
        return liquibase;
    }
}