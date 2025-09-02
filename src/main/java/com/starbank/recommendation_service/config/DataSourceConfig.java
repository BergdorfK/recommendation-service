package com.starbank.recommendation_service.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
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
    // H2 — дефолт
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource h2DataSource() { return DataSourceBuilder.create().build(); }

    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource h2) {
        return new JdbcTemplate(h2);
    }

    // Postgres — для динамических правил
    @Bean(name = "rulesDataSource")
    @ConfigurationProperties("rules.datasource")
    public DataSource rulesDataSource() { return DataSourceBuilder.create().build(); }

    @Bean(name = "rulesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean rulesEmf(@Qualifier("rulesDataSource") DataSource ds) {
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setPackagesToScan("com.starbank.recommendation_service.dynamic.model");
        em.setPersistenceUnitName("rules");
        var vendor = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendor);
        em.setJpaPropertyMap(Map.of(
                "hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect",
                "hibernate.hbm2ddl.auto","none"
        ));
        return em;
    }

    @Bean(name = "rulesTransactionManager")
    public PlatformTransactionManager rulesTx(@Qualifier("rulesEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}


