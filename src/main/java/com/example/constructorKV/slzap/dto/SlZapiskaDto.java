package com.example.constructorKV.slzap.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ
 *
 * Используется для передачи данных клиенту.
 * Содержит поля для отображения в выпадающем списке.
 *
 * @Builder - паттерн Builder
 * @Data - геттеры, сеттеры, toString
 */
@Data
@Builder
public class SlZapiskaDto {

    /** ID служебной записки */
    private Integer id;

    /** Обозначение служебной записки */
    private String obozn;

    /** Наименование служебной записки */
    private String naimSlZap;

    /** Статус (для отображения) */
    private String status;

    /** Флаг согласования */
    private Boolean isAgreed;

    /** Текст статуса согласования для отображения */
    private String agreedStatus;

    /** ID изделия (из IZDEL_SL_ZAP) */
    private Integer izdelieId;

    /** Наименование изделия (из IZDELIE.NAIM_IZD) */
    private String izdelieName;

    /** ID связи (IZDEL_SL_ZAP.ID) */
    private Integer izdelSlZapId;

    /**
     * Отображаемое имя для ComboBox
     * Формат: "Обозначение — Наименование [Изделие: X]"
     */
    public String getDisplayName() {
        String oboznPart = (obozn != null && !obozn.isEmpty()) ? obozn : "Без обозн.";
        String naimPart = (naimSlZap != null && !naimSlZap.isEmpty()) ? naimSlZap : "Без названия";
        String izdPart = (izdelieName != null && !izdelieName.isEmpty()) ? " [Изделие: " + izdelieName + "]" : "";
        String agreeMark = (isAgreed != null && isAgreed) ? " ✅" : " ⏳";
        return oboznPart + " — " + naimPart + izdPart + agreeMark;
    }
}