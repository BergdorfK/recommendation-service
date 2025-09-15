package com.starbank.recommendation_service.config;

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

    @Primary
    @Bean(name = "defaultDsProps")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties defaultDsProps() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource defaultDataSource(@Qualifier("defaultDsProps") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource h2) {
        return new JdbcTemplate(h2);
    }

    @Bean(name = "rulesDsProps")
    @ConfigurationProperties("rules.datasource")
    public DataSourceProperties rulesDsProps() {
        return new DataSourceProperties();
    }

    @Bean(name = "rulesDataSource")
    public DataSource rulesDataSource(@Qualifier("rulesDsProps") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean(name = "rulesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean rulesEmf(@Qualifier("rulesDataSource") DataSource ds) {
        var em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ds);
        em.setPackagesToScan("com.starbank.recommendation_service.dynamic.model");
        em.setPersistenceUnitName("rules");

        var vendor = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendor);
        em.setJpaPropertyMap(Map.of(
                "hibernate.hbm2ddl.auto", "none"
        ));
        return em;
    }

    @Bean(name = "rulesTransactionManager")
    public PlatformTransactionManager rulesTx(@Qualifier("rulesEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "rulesLiquibase")
    public SpringLiquibase rulesLiquibase(@Qualifier("rulesDataSource") DataSource dataSource) {
        SpringLiquibase lb = new SpringLiquibase();
        lb.setDataSource(dataSource);
        lb.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        lb.setDefaultSchema("public");
        return lb;
    }
}