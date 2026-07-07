package com.example.constructorKV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ГЛАВНЫЙ КЛАСС ПРИЛОЖЕНИЯ
 *
 * Точка входа в Spring Boot приложение "Конструктор КВ".
 * Отвечает за запуск всего приложения и инициализацию Spring-контекста.
 *
 * @SpringBootApplication - комбинированная аннотация, включающая:
 * - @Configuration - класс содержит определения бинов
 * - @EnableAutoConfiguration - автоматическая настройка Spring
 * - @ComponentScan - сканирование компонентов в пакете
 *
 * После запуска приложение доступно по адресу: http://localhost:8080
 * Swagger UI: http://localhost:8080/swagger-ui.html
 */
@SpringBootApplication
public class ConstructorKvApplication {

	/**
	 * Точка входа в приложение.
	 * SpringApplication.run() запускает встроенный Tomcat сервер,
	 * инициализирует все бины и поднимает приложение.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(ConstructorKvApplication.class, args);

		// Вывод приветственного баннера в консоль
		System.out.println("""
                
                ╔══════════════════════════════════════════════════════╗
                ║   🚀 Конструктор КВ API запущен!                    ║
                ║   📚 Swagger: http://localhost:8080/swagger-ui.html ║
                ║   🌳 Tree API: http://localhost:8080/api/tree       ║
                ╚══════════════════════════════════════════════════════╝
                """);
	}
}