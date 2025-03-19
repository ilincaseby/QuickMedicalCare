package com.quickmedicalcare.backend.config.jpaconfig;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.quickmedicalcare.backend.correlationDataDatabase.repositories",
        entityManagerFactoryRef = "userPatientDataCorrelationEntityManagerFactory",
        transactionManagerRef = "userPatientDataCorrelationTransactionManager"
)
public class UserPatientDataCorrelationDatabaseConfig {
    @Primary
    @Bean(name = "userCorrelationDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.correlation")
    public DataSource medicalHistoryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "userPatientDataCorrelationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean medicalHistoryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userCorrelationDataSource") DataSource medicalHistoryDataSource) {
        return builder
                .dataSource(medicalHistoryDataSource)
                .packages("com.quickmedicalcare.backend.correlationDataDatabase.entities")
                .persistenceUnit("public")
                .build();
    }

    @Primary
    @Bean(name = "userPatientDataCorrelationTransactionManager")
    public PlatformTransactionManager medicalHistoryTransactionManager(
            @Qualifier("userPatientDataCorrelationEntityManagerFactory") EntityManagerFactory medicalHistoryEntityManagerFactory) {
        return new JpaTransactionManager(medicalHistoryEntityManagerFactory);
    }
}
