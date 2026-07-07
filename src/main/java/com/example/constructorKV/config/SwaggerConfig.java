package com.example.constructorKV.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * КОНФИГУРАЦИЯ SWAGGER (OpenAPI)
 *
 * Настраивает документацию API в формате OpenAPI 3.0.
 * Swagger UI будет доступен по адресу: /swagger-ui.html
 * OpenAPI JSON: /v3/api-docs
 *
 * Аннотация @Configuration указывает Spring, что этот класс содержит
 * определения бинов (настройки).
 */
@Configuration
public class SwaggerConfig {

    /**
     * Создаёт бин OpenAPI с мета-информацией об API.
     * Используется для генерации документации в Swagger UI.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Конструктор КВ API")
                        .description("""
                                API для работы с конструкторской документацией.
                                
                                Основные возможности:
                                - Построение дерева навигации (Темы → Изделия → Группы)
                                - Работа со спецификациями
                                - Формирование комплектовочных ведомостей
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TANTK")
                                .email("support@tantk.ru")
                                .url("https://tantk.ru")));
    }
}