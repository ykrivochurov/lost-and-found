package ru.eastbanctech.board.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import ru.eastbanctech.board.web.security.RestAuthenticationEntryPoint;
import ru.eastbanctech.board.web.security.RestAuthenticationFailureHandler;
import ru.eastbanctech.board.web.security.RestAuthenticationSuccessHandler;

/**
 * Created with IntelliJ IDEA.
 * User: yuribulkin
 */

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"ru.eastbanctech.board.web.security"})
@PropertySource({"ldap.properties"})
public class LdapSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Autowired
    private UserDetailsContextMapper userDetailsContextMapper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).
                and().
        ldapAuthentication()
                .userDetailsContextMapper(userDetailsContextMapper)
                    .groupSearchBase(environment.getProperty("ldap.groups.base.dn"))
                    .groupSearchFilter(environment.getProperty("ldap.group.search.filter"))
                    .userSearchFilter(environment.getProperty("ldap.user.search.filter"))
                    .contextSource()
                        .managerDn(environment.getProperty("ldap.manager.dn"))
                        .managerPassword(environment.getProperty("ldap.manager.password"))
                        .url(environment.getProperty("ldap.url"))
                    .and();

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().exceptionHandling().authenticationEntryPoint(
                restAuthenticationEntryPoint()).and().authorizeRequests().antMatchers("/api/login",
                "/api/logout").permitAll().antMatchers("/api/**").authenticated().and().formLogin().loginPage(
                "/api/login").failureHandler(authenticationFailureHandler()).successHandler(
                authenticationSuccessHandler());
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {

        return new RestAuthenticationEntryPoint("/api/login");
    }

    @Bean
    public RestAuthenticationSuccessHandler authenticationSuccessHandler() {

        return new RestAuthenticationSuccessHandler();
    }

    @Bean
    public RestAuthenticationFailureHandler authenticationFailureHandler() {

        return new RestAuthenticationFailureHandler();
    }

}
