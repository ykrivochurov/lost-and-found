package ru.poteriashki.laf.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

@Configuration
@PropertySource("test.properties")
public class TestPropHolderConfig {
    @Bean
    public static PropertyPlaceholderConfigurer configurer() {
        PropertyPlaceholderConfigurer pph = new PropertyPlaceholderConfigurer();
        pph.setLocation(new ClassPathResource("test.properties"));
        return pph;
    }

}
