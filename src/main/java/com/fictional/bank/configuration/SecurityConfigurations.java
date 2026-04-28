package com.fictional.bank.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfigurations {

    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                        .authorizeHttpRequests(authz -> authz
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/openapi.yaml"
                                ).permitAll()
                                .requestMatchers(HttpMethod.POST, "/v1/users").permitAll()
                                .anyRequest().authenticated()
                        )
                        .csrf(csrf -> csrf.disable());
                return http.build();
        }
}