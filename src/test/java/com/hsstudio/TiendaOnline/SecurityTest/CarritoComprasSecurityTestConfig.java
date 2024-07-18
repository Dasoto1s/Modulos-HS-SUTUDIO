package com.hsstudio.TiendaOnline.SecurityTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
@Profile("!test")
public class CarritoComprasSecurityTestConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/carrito-compras/", "/pedidos/", "/pagos/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
