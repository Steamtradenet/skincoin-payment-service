package net.steamtrade.payment.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by sasha on 12.06.16.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories( basePackages = {"net.steamtrade.payment.backend"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
public class DatasourceConfig {

    public static final String STEAMTRADE_UNIT_NAME = "steamtrade";
    public static final String STEAMTRADE_DATASOURCE = "steamtradeDatasource";

    @Primary
    @Bean(name = STEAMTRADE_DATASOURCE)
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource steamtradeDataSource() {
        return DataSourceBuilder.create().driverClassName("root").url("jdbc:mysql://localhost:3306/payment_service?useUnicode=true&useFastDateParsing=false&characterEncoding=UTF-8")
                .driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(steamtradeDataSource())
                .packages("net.steamtrade.payment.backend")
                .persistenceUnit(STEAMTRADE_UNIT_NAME)
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
