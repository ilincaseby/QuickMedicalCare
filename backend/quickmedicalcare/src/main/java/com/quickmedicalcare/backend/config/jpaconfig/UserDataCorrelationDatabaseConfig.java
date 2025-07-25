package com.quickmedicalcare.backend.config.jpaconfig;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.quickmedicalcare.backend.correlationDataDatabase.repositories",
        entityManagerFactoryRef = "userCorrelationEntityManagerFactory",
        transactionManagerRef = "userCorrelationTransactionManager"
)
public class UserDataCorrelationDatabaseConfig {
    @Bean(name = "userCorrelationDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.correlation")
    public DataSource userCorrelationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "userCorrelationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userCorrelationEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userCorrelationDataSource") DataSource userCorrelationDataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//        properties.put("hibernate.show_sql", "true");
        return builder
                .dataSource(userCorrelationDataSource)
                .packages("com.quickmedicalcare.backend.correlationDataDatabase.entities")
                .persistenceUnit("public")
                .properties(properties)
                .build();
    }

    @Bean(name = "userCorrelationTransactionManager")
    public PlatformTransactionManager userCorrelationTransactionManager(
            @Qualifier("userCorrelationEntityManagerFactory") EntityManagerFactory userCorrelationEntityManagerFactory) {
        return new JpaTransactionManager(userCorrelationEntityManagerFactory);
    }
}
