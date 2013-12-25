package ru.poteriashki.laf.web.config;

/**
 * Created with IntelliJ IDEA.
 * User: Yuri A. Bulkin
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.poteriashki.laf.core.config.PropHolderConfig;
import ru.poteriashki.laf.web.config.jsonview.JsonViewSupportFactoryBean;
import ru.poteriashki.laf.web.config.jsonview.ViewAwareJsonMessageConverter;

import java.util.List;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ru.poteriashki.laf.web.controllers"},
        excludeFilters = {@ComponentScan.Filter(Configuration.class)})
@Import({PropHolderConfig.class, ThymeleafConfig.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("classpath:application.properties")
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @Bean
    public ViewResolver staticHTMLViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/html/");
        viewResolver.setSuffix(".html");
        viewResolver.setOrder(2);
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public JsonViewSupportFactoryBean jsonViewSupportFactoryBean() {
        return new JsonViewSupportFactoryBean();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {

        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<>());
//        messageConverters.add(mappingJackson2HttpMessageConverter);
        messageConverters.add(new ViewAwareJsonMessageConverter());
    }
}
