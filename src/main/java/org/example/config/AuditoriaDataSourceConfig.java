package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@EnableJpaRepositories(
        basePackages = "org.example.repository.auditoria",
        entityManagerFactoryRef = "auditoriaEntityManagerFactory",
        transactionManagerRef = "auditoriaTransactionManager"
)
public class AuditoriaDataSourceConfig {
    @Bean(name = "auditoriaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource auditoriaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "auditoriaJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa")
    public Map<String, String> auditoriaJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "auditoriaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean auditoriaEntityManagerFactory(
            @Qualifier("auditoriaDataSource") DataSource dataSource,
            @Qualifier("auditoriaJpaProperties") Map<String, String> auditoriaJpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.example.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        if (!auditoriaJpaProperties.containsKey("hibernate.dialect")) {
            auditoriaJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        }
        em.setJpaPropertyMap(auditoriaJpaProperties);
        return em;
    }

    @Bean(name = "auditoriaTransactionManager")
    public PlatformTransactionManager auditoriaTransactionManager(
            @Qualifier("auditoriaEntityManagerFactory") LocalContainerEntityManagerFactoryBean auditoriaEntityManagerFactory) {
        return new JpaTransactionManager(auditoriaEntityManagerFactory.getObject());
    }
}
