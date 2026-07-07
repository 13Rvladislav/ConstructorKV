package com.example.constructorKV.kv.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO ДЛЯ КОМПЛЕКТОВОЧНОЙ ВЕДОМОСТИ
 *
 * Используется для передачи данных клиенту.
 * Содержит информацию для отображения в выпадающем списке.
 *
 * @Builder - паттерн Builder
 * @Data - геттеры, сеттеры, toString
 */
@Data
@Builder
public class KomplektovkaDto {

    /** ID КВ */
    private Integer id;

    /** Проектное обозначение */
    private String design;

    /** ID служебной записки */
    private Integer idSz;

    /** Строка со сборками */
    private String sborkiSz;

    /** Дата создания (для отображения) */
    private String dateCreate;

    /** Статус (активна/архив) */
    private String status;

    /**
     * Отображаемое имя для ComboBox
     * Формат: "КВ-XXX — 12.06.2024"
     *
     * @return строка для отображения в выпадающем списке
     */
    public String getDisplayName() {
        String designPart = (design != null && !design.isEmpty()) ? design : "КВ-" + id;
        String datePart = (dateCreate != null) ? dateCreate : "Не сформирована";
        return designPart + " — " + datePart;
    }
}