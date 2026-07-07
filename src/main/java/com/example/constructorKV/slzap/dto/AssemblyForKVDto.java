package com.example.constructorKV.slzap.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO ДЛЯ СБОРКИ (СПЕЦИФИКАЦИИ) ИЗ СЛУЖЕБНОЙ ЗАПИСКИ
 *
 * Используется для отображения списка сборок при формировании КВ.
 *
 * @Builder - паттерн Builder
 * @Data - геттеры, сеттеры, toString
 */
@Data
@Builder
public class AssemblyForKVDto {

    /** ID спецификации (сборки) */
    private Integer specifId;

    /** Обозначение сборки */
    private String obozn;

    /** Наименование изделия */
    private String izdelieName;

    /** Статус: уже есть КВ на эту сборку? */
    private Boolean hasKv;

    /**
     * Отображаемое имя для выбора в диалоге
     * Формат: "Обозначение [Изделие: X]"
     */
    public String getDisplayName() {
        String oboznPart = (obozn != null && !obozn.isEmpty()) ? obozn : "Без обозн.";
        String izdPart = (izdelieName != null && !izdelieName.isEmpty()) ? " [Изделие: " + izdelieName + "]" : "";
        return oboznPart + izdPart;
    }
}