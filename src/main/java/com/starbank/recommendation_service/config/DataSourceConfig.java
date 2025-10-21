package com.starbank.recommendation_service.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.starbank.recommendation_service.dynamic.repository",
        entityManagerFactoryRef = "rulesEntityManagerFactory",
        transactionManagerRef = "rulesTransactionManager")
@EntityScan(basePackages = "com.starbank.recommendation_service.dynamic.model")
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("rules.datasource")
    public DataSourceProperties rulesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "rulesDataSource")
    @Primary
    public DataSource rulesDataSource(
            @Qualifier("rulesDataSourceProperties") DataSourceProperties props
    ) {
        HikariDataSource ds = props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        ds.setPoolName("RulesPool");
        ds.setMaximumPoolSize(10);
        return new LazyConnectionDataSourceProxy(ds);
    }

    @Bean
    public SpringLiquibase rulesLiquibase(
            @Qualifier("rulesDataSource") DataSource dataSource,
            RulesLiquibaseProperties props
    ) {
        SpringLiquibase lb = new SpringLiquibase();
        lb.setDataSource(dataSource);
        lb.setChangeLog(props.getChangeLog());
        if (props.getContexts() != null && !props.getContexts().isEmpty()) {
            lb.setContexts(String.join(",", props.getContexts()));
        }
        if (props.getLabels() != null && !props.getLabels().isEmpty()) {
            lb.setLabels(String.join(",", props.getLabels()));
        }
        lb.setShouldRun(props.isEnabled());
        lb.setDropFirst(props.isDropFirst());
        lb.setDefaultSchema(props.getDefaultSchema());
        lb.setLiquibaseSchema(props.getLiquibaseSchema());
        lb.setClearCheckSums(props.isClearCheckSums());
        lb.setChangeLogParameters(props.getParameters());
        return lb;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean rulesEntityManagerFactory(
            @Qualifier("rulesDataSource") DataSource dataSource
    ) {
        var emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.starbank.recommendation_service.dynamic.model");
        emf.setPersistenceUnitName("rules");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> jpa = new HashMap<>();
        jpa.put("hibernate.hbm2ddl.auto", "none");
        jpa.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        jpa.put("hibernate.show_sql", "false");
        jpa.put("hibernate.format_sql", "false");

        emf.setJpaPropertyMap(jpa);
        return emf;
    }

    @Bean
    public PlatformTransactionManager rulesTransactionManager(
            @Qualifier("rulesEntityManagerFactory") LocalContainerEntityManagerFactoryBean emf
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(emf.getObject()));
    }

    @Configuration
    @ConfigurationProperties(prefix = "rules.liquibase")
    public static class RulesLiquibaseProperties {
        private boolean enabled = true;
        private String changeLog = "classpath:db/changelog/db.changelog-master.sql";
        private String defaultSchema;
        private String liquibaseSchema;
        private boolean dropFirst = false;
        private boolean clearCheckSums = true;
        private List<String> contexts;
        private List<String> labels;
        private Map<String, String> parameters = new HashMap<>();

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getChangeLog() { return changeLog; }
        public void setChangeLog(String changeLog) { this.changeLog = changeLog; }
        public String getDefaultSchema() { return defaultSchema; }
        public void setDefaultSchema(String defaultSchema) { this.defaultSchema = defaultSchema; }
        public String getLiquibaseSchema() { return liquibaseSchema; }
        public void setLiquibaseSchema(String liquibaseSchema) { this.liquibaseSchema = liquibaseSchema; }
        public boolean isDropFirst() { return dropFirst; }
        public void setDropFirst(boolean dropFirst) { this.dropFirst = dropFirst; }
        public boolean isClearCheckSums() { return clearCheckSums; }
        public void setClearCheckSums(boolean clearCheckSums) { this.clearCheckSums = clearCheckSums; }
        public List<String> getContexts() { return contexts; }
        public void setContexts(List<String> contexts) { this.contexts = contexts; }
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        public Map<String, String> getParameters() { return parameters; }
        public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }
    }
}