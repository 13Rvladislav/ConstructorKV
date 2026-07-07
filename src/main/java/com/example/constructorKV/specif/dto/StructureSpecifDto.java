package com.example.constructorKV.specif.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO ДЛЯ ЭЛЕМЕНТА СТРУКТУРЫ СПЕЦИФИКАЦИИ
 *
 * Используется для передачи структуры спецификации клиенту.
 * Группируется по разделам (ID_DIVISION).
 *
 * @Builder - паттерн Builder
 * @Data - геттеры, сеттеры, toString
 */
@Data
@Builder
public class StructureSpecifDto {

    /** ID записи в STRUCTURE_SPECIF */
    private Integer id;

    /** ID спецификации-владельца */
    private Integer idSpecif;

    /** Обозначение */
    private String obozn;

    /** Наименование */
    private String naim;

    /** Количество */
    private Integer quantity;

    /** Масса (RMASSA) */
    private Double massa;

    /** Материал */
    private String material;

    /** ID раздела (2-документация, 3-сборки, 4-детали, 5-стандартные, 6-покупные, 7-материалы) */
    private Integer idDivision;

    /** Позиция */
    private String pos;

    /** Формат */
    private String format;

    /** Зона */
    private String zone;
}