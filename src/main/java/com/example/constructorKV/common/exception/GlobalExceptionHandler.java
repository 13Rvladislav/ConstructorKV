package com.example.constructorKV.common.exception;

import com.example.constructorKV.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ГЛОБАЛЬНЫЙ ОБРАБОТЧИК ИСКЛЮЧЕНИЙ
 *
 * Перехватывает все исключения, возникающие в контроллерах,
 * и преобразует их в единый формат ApiResponse.
 *
 * Аннотация @RestControllerAdvice:
 * - @ControllerAdvice - глобальный перехватчик для всех контроллеров
 * - @ResponseBody - автоматически сериализует ответ в JSON
 *
 * Слой: PRESENTATION (обработка ошибок)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обработчик всех непредвиденных исключений.
     * Срабатывает, когда исключение не обработано другими хендлерами.
     *
     * @param e исключение
     * @return ответ с ошибкой 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        log.error("❌ Необработанное исключение: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Внутренняя ошибка сервера: " + e.getMessage()));
    }

    /**
     * Обработчик бизнес-исключений.
     * Возвращает понятное пользователю сообщение с кодом 400.
     *
     * @param e бизнес-исключение
     * @return ответ с ошибкой 400
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException e) {
        log.error("❌ Бизнес-ошибка: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), 400));
    }

    /**
     * Обработчик RuntimeException.
     * Срабатывает, если исключение не является BusinessException.
     *
     * @param e исключение времени выполнения
     * @return ответ с ошибкой 500
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        log.error("❌ Ошибка выполнения: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Ошибка: " + e.getMessage()));
    }
}