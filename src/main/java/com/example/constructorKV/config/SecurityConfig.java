package com.example.constructorKV.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * КОНФИГУРАЦИЯ БЕЗОПАСНОСТИ (ВРЕМЕННО ОТКЛЮЧЕНА)
 *
 * Для разработки отключаем аутентификацию.
 * В production нужно будет включить обратно.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Разрешаем все запросы без аутентификации.
     *
     * @param http объект HttpSecurity для настройки
     * @return SecurityFilterChain
     * @throws Exception если ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Отключаем CSRF для разработки
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // Разрешаем все запросы
                )
                .formLogin(form -> form.disable())   // Отключаем форму логина
                .httpBasic(httpBasic -> httpBasic.disable()); // Отключаем Basic Auth

        return http.build();
    }
}