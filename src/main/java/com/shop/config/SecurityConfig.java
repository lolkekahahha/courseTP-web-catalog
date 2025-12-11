package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// аутентификация, авторизация, защита URL, шифрование паролей

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // бин для шифрования паролей BCrypt 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // конфигурация цепочки фильтров безопасности
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // публичные страницы (доступны всем)
                .requestMatchers("/", "/register", "/login", "/products", "/css/**", "/images/**", "/uploads/**").permitAll()
                // панель продавца (только для роли SELLER)
                .requestMatchers("/seller/**", "/api/upload/**").hasRole("SELLER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/upload/**")
            );
        
        return http.build();
    }
}
