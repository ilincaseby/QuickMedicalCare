package com.quickmedicalcare.backend.config.securityConfig;

import com.quickmedicalcare.backend.RegisterLogin.SuperTokensInterface;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private SuperTokensFilter superTokensFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(superTokensFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers("/test/api/**").permitAll()
                .requestMatchers("/api/v1/authentication/**").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
}


*.bin
NLP/venv/
NLP/transformer/results/
NLP/transformer/tinybert_symptoms_classifier/
NLP/transformer/tinybert_symptoms_classifier_tokenizer/
NLP/vsm/GoogleNews-vectors-negative300.bin
MLTraining/venv/
MLTraining/huggingface_dataset