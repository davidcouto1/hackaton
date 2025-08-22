package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
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
        basePackages = "org.example.repository.produto",
        entityManagerFactoryRef = "produtoEntityManagerFactory",
        transactionManagerRef = "produtoTransactionManager"
)
public class ProdutoDataSourceConfig {
    @Bean(name = "produtoDataSource")
    @ConfigurationProperties(prefix = "spring.produto-datasource")
    public DataSource produtoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "produtoJpaProperties")
    @ConfigurationProperties(prefix = "spring.produto-datasource.jpa")
    public Map<String, String> produtoJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "produtoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean produtoEntityManagerFactory(
            @Qualifier("produtoDataSource") DataSource dataSource,
            @Qualifier("produtoJpaProperties") Map<String, String> produtoJpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.example.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // Adiciona manualmente o dialect se não estiver presente
        if (!produtoJpaProperties.containsKey("hibernate.dialect")) {
            produtoJpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        }
        em.setJpaPropertyMap(produtoJpaProperties);
        return em;
    }

    @Bean(name = "produtoTransactionManager")
    public PlatformTransactionManager produtoTransactionManager(
            @Qualifier("produtoEntityManagerFactory") LocalContainerEntityManagerFactoryBean produtoEntityManagerFactory) {
        return new JpaTransactionManager(produtoEntityManagerFactory.getObject());
    }
}
