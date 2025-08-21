package org.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

@Configuration
@EnableJpaRepositories(
        basePackages = "org.example.repository",
        entityManagerFactoryRef = "produtoEntityManagerFactory",
        transactionManagerRef = "produtoTransactionManager"
)
public class ProdutoDataSourceConfig {
    @Bean(name = "produtoDataSource")
    @ConfigurationProperties(prefix = "spring.produto-datasource")
    public DataSource produtoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "produtoEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean produtoEntityManagerFactory(
            @Qualifier("produtoDataSource") DataSource dataSource,
            JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.example.model.produto");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(new HashMap<>(jpaProperties.getProperties()));
        return em;
    }

    @Bean(name = "produtoTransactionManager")
    public PlatformTransactionManager produtoTransactionManager(
            @Qualifier("produtoEntityManagerFactory") LocalContainerEntityManagerFactoryBean produtoEntityManagerFactory) {
        return new JpaTransactionManager(produtoEntityManagerFactory.getObject());
    }
}
