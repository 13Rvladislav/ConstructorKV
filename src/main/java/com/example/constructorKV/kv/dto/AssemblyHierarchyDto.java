// src/main/java/com/example/constructorKV/kv/dto/AssemblyHierarchyDto.java
package com.example.constructorKV.kv.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO ДЛЯ ИЕРАРХИИ СБОРОК
 *
 * Используется для отображения дерева сборок в окне предпросмотра.
 * Содержит полную иерархию с родительскими и дочерними связями.
 */
@Data
@Builder
public class AssemblyHierarchyDto {

    /** ID спецификации */
    private Integer id;

    /** Обозначение сборки */
    private String obozn;

    /** Наименование */
    private String naim;

    /** Количество */
    private Integer quantity;

    /** Масса */
    private Double massa;

    /** ID родительской сборки (null = корень) */
    private Integer parentId;

    /** Уровень вложенности (0 = корень) */
    private Integer level;

    /** ID структуры из STRUCTURE_SPECIF */
    private Integer structureId;

    /** Номер изделия (числовой, например 101) */
    private Integer izdelieNumber;

    /** ID изделия (из таблицы IZDELIE) */
    private Integer izdelieId;

    /** Статус: подходит ли под серию (true = подходит, false = не подходит) */
    private Boolean fitsSeries;

    /** Причина несоответствия (для отладки) */
    private String fitReason;

    /** Список серий для этой сборки */
    @Builder.Default
    private List<SeriesInfo> series = new ArrayList<>();

    /** Список дочерних сборок */
    @Builder.Default
    private List<AssemblyHierarchyDto> children = new ArrayList<>();

    /**
     * Добавляет дочернюю сборку
     */
    public void addChild(AssemblyHierarchyDto child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    /**
     * Добавляет серию
     */
    public void addSeries(SeriesInfo seriesInfo) {
        if (this.series == null) {
            this.series = new ArrayList<>();
        }
        this.series.add(seriesInfo);
    }

    /**
     * Проверяет, подходит ли сборка для указанного изделия
     *
     * Важно: если узел уже помечен как неподходящий (родителем),
     * метод не перезаписывает этот статус.
     *
     * @param productNumber НОМЕР изделия (например, 101)
     * @return true если подходит
     */
    public boolean checkFitsSeries(Integer productNumber) {
        // Если узел уже помечен как неподходящий (родителем) — не перезаписываем
        if (this.fitsSeries != null && !this.fitsSeries) {
            return false;
        }

        if (productNumber == null) {
            this.fitsSeries = true;
            this.fitReason = "Нет номера изделия";
            return true;
        }

        // Если серий нет — подходит для всех изделий
        if (series == null || series.isEmpty()) {
            this.fitsSeries = true;
            this.fitReason = "Нет серий (действует на все)";
            return true;
        }

        // Проверяем каждую серию по НОМЕРУ изделия
        for (SeriesInfo s : series) {
            if (s.getMini() != null && s.getMaxi() != null) {
                if (productNumber >= s.getMini() && productNumber <= s.getMaxi()) {
                    this.fitsSeries = true;
                    this.fitReason = "Подходит (серия " + s.getMini() + "–" + s.getMaxi() + ")";
                    return true;
                }
            }
        }

        this.fitsSeries = false;
        this.fitReason = "Не подходит (изделие " + productNumber + " вне диапазона серий)";
        return false;
    }

    /**
     * ИНФОРМАЦИЯ О СЕРИИ
     */
    @Data
    @Builder
    public static class SeriesInfo {
        /** Начало диапазона */
        private Integer mini;

        /** Конец диапазона */
        private Integer maxi;

        /** Количество */
        private Integer quantity;

        /** Масса */
        private Double massa;

        /** Габариты */
        private String dimensions;

        /**
         * Проверяет, попадает ли изделие в диапазон серии
         */
        public boolean containsProduct(Integer productNumber) {
            if (productNumber == null) return false;
            if (mini != null && maxi != null) {
                return productNumber >= mini && productNumber <= maxi;
            }
            return false;
        }
    }
}