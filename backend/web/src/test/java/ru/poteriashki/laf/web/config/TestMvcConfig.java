package ru.poteriashki.laf.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.eastbanctech.resources.config.MongoConnectionConfig;
import ru.eastbanctech.resources.config.MongoResourceServiceConfig;
import ru.poteriashki.laf.core.config.BaseConfiguration;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.poteriashki.laf.web.controllers"},
        excludeFilters = {@ComponentScan.Filter(Configuration.class)})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({TestPropHolderConfig.class, BaseConfiguration.class,
        MongoConnectionConfig.class, MongoResourceServiceConfig.class})
@PropertySource("classpath:test.properties")
public class TestMvcConfig extends WebMvcConfigurerAdapter {

}
