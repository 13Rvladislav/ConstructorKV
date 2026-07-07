package com.example.constructorKV.kv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO ДЛЯ ЗАПРОСА НА ФОРМИРОВАНИЕ КВ
 *
 * Используется в POST /api/kv/form для передачи параметров.
 * Содержит всю информацию, необходимую для создания новой КВ.
 *
 * @Builder - паттерн Builder для удобного создания
 * @Data - генерирует геттеры, сеттеры, toString, equals, hashCode
 * @NoArgsConstructor - конструктор без параметров (нужен Jackson для десериализации)
 * @AllArgsConstructor - конструктор со всеми параметрами (нужен Builder)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KVFormRequestDto {

    /**
     * ID служебной записки, на которую формируется КВ
     * Обязательное поле
     */
    private Integer slZapiskaId;

    /**
     * ID сборки (спецификации), на которую формируется КВ
     * Обязательное поле
     */
    private Integer specifId;

    /**
     * ID пользователя, формирующего КВ
     * Будет записан в поле FORMED_BY
     */
    private Integer formedBy;
}