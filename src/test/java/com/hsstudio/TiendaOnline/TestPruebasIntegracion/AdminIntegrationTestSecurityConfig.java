package com.hsstudio.TiendaOnline.TestPruebasIntegracion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
@Profile("test")
public class AdminIntegrationTestSecurityConfig {
    @Bean
    public SecurityFilterChain adminTestFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .httpBasic().disable();

        System.out.println("AdminIntegrationTestSecurityConfig is applied");
        return http.build();
    }
}