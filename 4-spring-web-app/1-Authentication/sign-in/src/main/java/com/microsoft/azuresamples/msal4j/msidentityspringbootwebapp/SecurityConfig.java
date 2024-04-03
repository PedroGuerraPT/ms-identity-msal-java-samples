// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadResourceServerHttpSecurityConfigurer;
import com.azure.spring.cloud.autoconfigure.implementation.aad.security.AadWebApplicationHttpSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig  {

    @Value("${app.protect.authenticated}")
    private String[] allowedOrigins;

    @Bean
    @Order(1)
    SecurityFilterChain apiFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Deprecated but the only way it works
        httpSecurity.authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers("/**")
                                .permitAll()
                                .anyRequest().authenticated()
                )
                // CSRF is not needed as an Authorization Token is being used for authentication
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .apply(AadResourceServerHttpSecurityConfigurer.aadResourceServer());
        return httpSecurity.build();
    }

    @Bean
    public SecurityFilterChain htmlFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeHttpRequests(configurer -> configurer
                        .requestMatchers("/**")
                        .permitAll()
                        .authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .apply(AadWebApplicationHttpSecurityConfigurer.aadWebApplication());
        // @formatter:on
        return http.build();
    }

}
