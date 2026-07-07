package com.example.constructorKV.specif.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO ДЛЯ УЗЛА СПЕЦИФИКАЦИИ
 *
 * Содержит только то, что нужно для отображения:
 * - ID, родитель, обозначение, наименование
 * - Флаг заимствования
 */
@Data
@Builder
public class SpecifNodeDto {

    /** ID спецификации */
    private Integer id;

    /** ID родителя (null = корень) */
    private Integer parentId;

    /** Обозначение (показываемое) */
    private String obozn;

    /** Наименование (показываемое) */
    private String naim;

    /** Является ли заимствованной */
    @Builder.Default
    private Boolean isLinked = false;

    /** Информация о заимствовании (для отображения в UI) */
    private String linkInfo;

    /** Список дочерних узлов */
    @Builder.Default
    private List<SpecifNodeDto> children = new ArrayList<>();
}