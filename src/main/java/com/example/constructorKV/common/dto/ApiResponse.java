package com.example.constructorKV.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * УНИВЕРСАЛЬНЫЙ ОТВЕТ API
 *
 * Стандартизированный формат ответа для всех REST-эндпоинтов.
 * Обеспечивает единообразную структуру ответа для клиентов.
 *
 * @param <T> тип данных, которые возвращаются в поле "data"
 *
 * Пример успешного ответа:
 * {
 *   "success": true,
 *   "message": "Данные получены",
 *   "data": { ... },
 *   "timestamp": "2026-06-22T10:30:00",
 *   "statusCode": 200
 * }
 *
 * Пример ответа с ошибкой:
 * {
 *   "success": false,
 *   "message": "Тема не найдена",
 *   "data": null,
 *   "timestamp": "2026-06-22T10:30:00",
 *   "statusCode": 404
 * }
 *
 * @Data - генерирует геттеры, сеттеры, toString
 * @Builder - паттерн Builder
 * @NoArgsConstructor - конструктор без параметров
 * @AllArgsConstructor - конструктор со всеми параметрами
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Флаг успешности выполнения запроса */
    private boolean success;

    /** Сообщение для пользователя (успех или описание ошибки) */
    private String message;

    /** Основные данные ответа (может быть null при ошибке) */
    private T data;

    /** Время формирования ответа в формате ISO 8601 */
    private String timestamp;

    /** HTTP статус код ответа */
    private Integer statusCode;

    /**
     * Создаёт успешный ответ с данными.
     *
     * @param data данные для возврата
     * @param message сообщение об успехе
     * @param <T> тип данных
     * @return ApiResponse с флагом success = true
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .statusCode(200)
                .build();
    }

    /**
     * Создаёт ответ с ошибкой (стандартная ошибка сервера).
     *
     * @param message описание ошибки
     * @param <T> тип данных (обычно String)
     * @return ApiResponse с флагом success = false и статусом 500
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .statusCode(500)
                .build();
    }

    /**
     * Создаёт ответ с ошибкой и указанным HTTP статусом.
     *
     * @param message описание ошибки
     * @param statusCode HTTP статус код (400, 404, 500 и т.д.)
     * @param <T> тип данных (обычно String)
     * @return ApiResponse с флагом success = false
     */
    public static <T> ApiResponse<T> error(String message, Integer statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .statusCode(statusCode)
                .build();
    }
}