package com.example.constructorKV.tree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO ДЛЯ ПОЛНОГО ДЕРЕВА
 *
 * Используется в методах:
 * - GET /api/tree/full
 * - GET /api/tree/tema/{temaId}
 *
 * Содержит полную вложенную структуру: Темы → Изделия → Группы
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullTreeResponseDto {

    /** Список тем с полной структурой */
    @Builder.Default
    private List<TemaNode> temas = new ArrayList<>();

    /** Общее количество узлов */
    private long totalNodes;

    /** Версия */
    @Builder.Default
    private String version = "1.0";

    public static FullTreeResponseDto empty() {
        return FullTreeResponseDto.builder()
                .temas(new ArrayList<>())
                .totalNodes(0)
                .version("1.0")
                .build();
    }

    /**
     * УЗЕЛ ТЕМЫ (с изделиями)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemaNode {
        private Integer id;
        private String name;
        private Integer indeksKd;
        @Builder.Default
        private List<IzdelieNode> izdelia = new ArrayList<>();
    }

    /**
     * УЗЕЛ ИЗДЕЛИЯ (с группами)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IzdelieNode {
        private Integer id;
        private String name;
        private String fullName;
        private String dateAdd;
        @Builder.Default
        private List<GroupeNode> groupes = new ArrayList<>();
    }

    /**
     * УЗЕЛ ГРУППЫ
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupeNode {
        private Integer id;
        private String name;
    }
}