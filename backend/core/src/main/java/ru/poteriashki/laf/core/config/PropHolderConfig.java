package ru.poteriashki.laf.core.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * User: y.krivochurov
 * Date: 05.10.12
 * Time: 14:53
 */
@Configuration
@PropertySource({"application.properties"})
public class PropHolderConfig {

    @Bean
    public static PropertyPlaceholderConfigurer configurer() {

        PropertyPlaceholderConfigurer pph = new PropertyPlaceholderConfigurer();
        pph.setLocations(new Resource[]{new ClassPathResource("application.properties")});
        return pph;
    }

}
