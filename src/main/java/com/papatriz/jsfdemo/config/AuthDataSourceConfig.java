package com.papatriz.jsfdemo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.papatriz.jsfdemo.repositories.auth",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef= "authTransactionManager")
public class AuthDataSourceConfig {
    @Bean
    @ConfigurationProperties("auth.datasource")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("auth.datasource.configuration")
    public DataSource authDataSource() {
        return authDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(authDataSource())
                .packages("com.papatriz.jsfdemo.models.auth")
                .build();
    }

    @Bean
    public PlatformTransactionManager authTransactionManager(
            final @Qualifier("authEntityManagerFactory") LocalContainerEntityManagerFactoryBean authEntityManagerFactory) {
        return new JpaTransactionManager(authEntityManagerFactory.getObject());
    }
}
