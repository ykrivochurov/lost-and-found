package ru.eastbanctech.board.web.config;


import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import ru.eastbanctech.board.core.config.BaseConfiguration;
import ru.eastbanctech.board.core.config.InitConfiguration;
import ru.eastbanctech.board.core.config.PersistenceJPAConfig;
import ru.eastbanctech.board.core.config.PropHolderConfig;
import ru.eastbanctech.reports.config.ReportsServiceConfig;
import ru.eastbanctech.resources.config.MongoConnectionConfig;
import ru.eastbanctech.resources.config.MongoResourceServiceConfig;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Import({BaseConfiguration.class, PropHolderConfig.class, PersistenceJPAConfig.class,
        InitConfiguration.class, MongoConnectionConfig.class, MongoResourceServiceConfig.class,
        ReportsServiceConfig.class, LocalizationConfig.class})
@PropertySource("classpath:application.properties")
public class ApplicationContext extends WebMvcConfigurerAdapter {

    @Autowired
    private LocaleChangeInterceptor localeChangeInterceptor;

    @Bean
    public CommonsMultipartResolver multipartResolver() {

        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(52428800);
        resolver.setMaxInMemorySize(5242880);

        return resolver;
    }

//    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));

        return converter;
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(mappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor);
    }
}
