package com.example.constructorKV.kv.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AssemblyHierarchyDto {

    // ========================================================================
    // ОСНОВНЫЕ ПОЛЯ
    // ========================================================================

    private Integer id;
    private Integer idSpecif;
    private Integer parentId;
    private String obozn;
    private String naim;
    private Integer quantity;
    private Double massa;
    private Integer idDivision;
    private String material;
    private String note;
    private Integer level;
    private Integer idRootSpecif;
    private Integer idOriginalStructure;
    private Integer structureId;

    // ========================================================================
    // ДОПОЛНИТЕЛЬНЫЕ ПОЛЯ
    // ========================================================================

    private Integer izdelieNumber;
    private Integer izdelieId;
    private Boolean fitsSeries;
    private String fitReason;

    @Builder.Default
    private List<SeriesInfo> series = new ArrayList<>();

    @Builder.Default
    private List<AssemblyHierarchyDto> children = new ArrayList<>();

    // ========================================================================
    // МЕТОДЫ
    // ========================================================================

    public void addChild(AssemblyHierarchyDto child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    public void addSeries(SeriesInfo seriesInfo) {
        if (this.series == null) {
            this.series = new ArrayList<>();
        }
        this.series.add(seriesInfo);
    }

    /**
     * ПРОВЕРКА СЕРИЙНОСТИ ДЛЯ УЗЛА
     * Используется для сборок и их содержимого
     */
    public boolean checkFitsSeries(Integer productNumber) {
        if (productNumber == null) {
            this.fitsSeries = true;
            this.fitReason = "Нет номера изделия";
            return true;
        }

        if (series == null || series.isEmpty()) {
            this.fitsSeries = true;
            this.fitReason = "Нет серий (действует на все)";
            return true;
        }

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

    public String getTypeIcon() {
        if (idDivision == null) return "📄";
        return switch (idDivision) {
            case 2 -> "📄";
            case 3 -> "📦";
            case 4 -> "⚙️";
            case 5 -> "🔩";
            case 6 -> "🛒";
            case 7 -> "🧪";
            default -> "📄";
        };
    }

    public String getTypeName() {
        if (idDivision == null) return "НЕИЗВЕСТНО";
        return switch (idDivision) {
            case 2 -> "ДОКУМЕНТАЦИЯ";
            case 3 -> "СБОРКА";
            case 4 -> "ДЕТАЛЬ";
            case 5 -> "СТАНДАРТНОЕ";
            case 6 -> "ПОКУПНОЕ";
            case 7 -> "МАТЕРИАЛ";
            default -> "НЕИЗВЕСТНО";
        };
    }

    // ========================================================================
    // ВЛОЖЕННЫЙ КЛАСС: СЕРИЯ
    // ========================================================================

    @Data
    @Builder
    public static class SeriesInfo {
        private Integer mini;
        private Integer maxi;
        private Integer quantity;
        private Double massa;
        private String dimensions;

        public boolean containsProduct(Integer productNumber) {
            if (productNumber == null) return false;
            if (mini != null && maxi != null) {
                return productNumber >= mini && productNumber <= maxi;
            }
            return false;
        }
    }
}