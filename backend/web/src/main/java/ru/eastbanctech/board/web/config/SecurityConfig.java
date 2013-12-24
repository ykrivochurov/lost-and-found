package ru.eastbanctech.board.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.eastbanctech.board.web.security.RestAuthenticationEntryPoint;
import ru.eastbanctech.board.web.security.RestAuthenticationFailureHandler;
import ru.eastbanctech.board.web.security.RestAuthenticationSuccessHandler;

/**
 * Created with IntelliJ IDEA.
 * User: y.bulkin
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().exceptionHandling().authenticationEntryPoint(
                restAuthenticationEntryPoint()).and().authorizeRequests().antMatchers("/api/login",
                "/api/logout").permitAll().antMatchers("/api/**").authenticated().and().formLogin().loginPage(
                "/api/login").failureHandler(authenticationFailureHandler()).successHandler(
                authenticationSuccessHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsServiceBean());
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
