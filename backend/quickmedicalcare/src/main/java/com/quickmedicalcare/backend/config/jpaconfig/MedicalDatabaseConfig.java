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
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.quickmedicalcare.backend.medicalDataDatabase.repositories",
        entityManagerFactoryRef = "medicalEntityManagerFactory",
        transactionManagerRef = "medicalTransactionManager"
)
public class MedicalDatabaseConfig {
    @Primary
    @Bean(name = "medicalDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.medical")
    public DataSource medicalDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "medicalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean medicalEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("medicalDataSource") DataSource medicalDataSource) {
        return builder
                .dataSource(medicalDataSource)
                .packages("com.quickmedicalcare.backend.medicalDataDatabase.entities")
                .persistenceUnit("medical")
                .build();
    }

    @Primary
    @Bean(name = "medicalTransactionManager")
    public PlatformTransactionManager medicalTransactionManager(
            @Qualifier("medicalEntityManagerFactory") EntityManagerFactory medicalEntityManagerFactory) {
        return new JpaTransactionManager(medicalEntityManagerFactory);
    }
}
