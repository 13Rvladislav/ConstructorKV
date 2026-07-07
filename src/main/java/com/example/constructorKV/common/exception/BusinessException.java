package com.example.constructorKV.common.exception;

/**
 * БИЗНЕС-ИСКЛЮЧЕНИЕ
 *
 * Кастомное исключение для обработки бизнес-ошибок в приложении.
 * Используется для ситуаций, которые можно предвидеть и обработать:
 * - Объект не найден в БД
 * - Нарушение бизнес-правил
 * - Некорректные входные данные
 *
 * Отличается от RuntimeException тем, что содержит понятное
 * пользователю сообщение об ошибке.
 */
public class BusinessException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message понятное пользователю описание ошибки
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     *
     * @param message понятное пользователю описание ошибки
     * @param cause исходная причина (для логирования)
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}