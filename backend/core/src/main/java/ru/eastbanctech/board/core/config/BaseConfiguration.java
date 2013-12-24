package ru.eastbanctech.board.core.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * User: y.krivochurov
 * Date: 28.04.13
 * Time: 11:27
 */
@Configuration
@EnableCaching
@EnableScheduling
@EnableJpaRepositories(value = {"ru.eastbanctech.board.core.dao", "ru.eastbanctech.board.core.dao.impl"})
//@ImportResource({"classpath*:spring/springDataConfig.xml", "classpath*:spring/spring-security.xml"})
@ComponentScan(basePackages = {"ru.eastbanctech.board.core.service"})
public class BaseConfiguration {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        String[] resources = {"classpath:labels", "classpath:messages", "classpath:ValidationMessages"};
        messageSource.setBasenames(resources);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache-general.xml"));
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
        return cacheManager;
    }


}
