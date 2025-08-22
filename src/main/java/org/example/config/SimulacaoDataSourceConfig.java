package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "org.example.repository.simulacao",
        entityManagerFactoryRef = "simulacaoEntityManagerFactory",
        transactionManagerRef = "simulacaoTransactionManager"
)
public class SimulacaoDataSourceConfig {
    @Bean(name = "simulacaoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource simulacaoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "simulacaoJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa")
    public Map<String, String> simulacaoJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "simulacaoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean simulacaoEntityManagerFactory(
            @Qualifier("simulacaoDataSource") DataSource dataSource,
            @Qualifier("simulacaoJpaProperties") Map<String, String> simulacaoJpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.example.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // Adiciona manualmente o dialect se não estiver presente
        if (!simulacaoJpaProperties.containsKey("hibernate.dialect")) {
            simulacaoJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        }
        em.setJpaPropertyMap(simulacaoJpaProperties);
        return em;
    }

    @Bean(name = "simulacaoTransactionManager")
    public PlatformTransactionManager simulacaoTransactionManager(
            @Qualifier("simulacaoEntityManagerFactory") LocalContainerEntityManagerFactoryBean simulacaoEntityManagerFactory) {
        return new JpaTransactionManager(simulacaoEntityManagerFactory.getObject());
    }
}
