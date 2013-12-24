package ru.eastbanctech.board.core.config;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:15
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"ru.eastbanctech.board.core.dao","ru.eastbanctech.board.core.dao.impl"})
public class PersistenceJPAConfig {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(this.dataSource());
        factoryBean.setPackagesToScan("ru.eastbanctech.board.core.model");

        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaPropertyMap(jpaProperties());

        return factoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform(MySQL5InnoDBDialect.class.getName());
        return hibernateJpaVendorAdapter;
    }


    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        return dataSource;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager(this.entityManagerFactory().getObject());
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator(){
        return new HibernateExceptionTranslator();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        props.put("hibernate.cache.use_second_level_cache", "true");
        props.put("hibernate.cache.use_query_cache", "true");
        props.put("hibernate.connection.autocommit", "auto");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.max_fetch_depth", "0");
        props.put("hibernate.default_batch_fetch_size", "10");
        props.put("hibernate.ejb.naming_strategy", "ru.eastbanctech.board.core.dao.CustomNamingStrategy");
        // todo only for testing
        props.put("hibernate.hbm2ddl.auto", "create");
        return props;
    }

}
