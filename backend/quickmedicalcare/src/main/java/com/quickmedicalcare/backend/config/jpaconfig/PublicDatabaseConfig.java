package com.quickmedicalcare.backend.config.jpaconfig;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
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
        basePackages = "com.quickmedicalcare.backend.publicDataDatabase.repositories",
        entityManagerFactoryRef = "publicEntityManagerFactory",
        transactionManagerRef = "publicTransactionManager"
)
public class PublicDatabaseConfig {
    @Primary
    @Bean(name = "publicDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.public")
    public DataSource publicDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "publicEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean publicEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("publicDataSource") DataSource publicDataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//        properties.put("hibernate.show_sql", "true");
        return builder
                .dataSource(publicDataSource)
                .packages("com.quickmedicalcare.backend.publicDataDatabase.entities")
                .persistenceUnit("public")
                .properties(properties)
                .build();
    }


    @Primary
    @Bean(name = "publicTransactionManager")
    public PlatformTransactionManager publicTransactionManager(
            @Qualifier("publicEntityManagerFactory") EntityManagerFactory publicEntityManagerFactory) {
        return new JpaTransactionManager(publicEntityManagerFactory);
    }
}
