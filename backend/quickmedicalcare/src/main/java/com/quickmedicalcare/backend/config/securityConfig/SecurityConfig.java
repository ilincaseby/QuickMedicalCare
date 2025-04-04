package com.quickmedicalcare.backend.config.securityConfig;

import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private SuperTokensFilter superTokensFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/authentication/register").permitAll()
                        .requestMatchers("/api/v1/authentication/login").permitAll()
                        .requestMatchers("/api/v1/prognosis").permitAll()
                        .requestMatchers("/test/api/**").permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(superTokensFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}