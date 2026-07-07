package com.example.constructorKV.tree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO ДЛЯ ИНФОРМАЦИИ О ТЕМЕ
 *
 * Используется в методе:
 * - GET /api/tree/tema/{temaId}/info
 *
 * Содержит ТОЛЬКО информацию о теме, БЕЗ вложенных изделий.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemaInfoResponseDto {

    /** Список тем (только информация, без изделий) */
    @Builder.Default
    private List<TemaInfoNode> temas = new ArrayList<>();

    /** Количество найденных тем */
    private long totalNodes;

    /** Версия */
    @Builder.Default
    private String version = "1.0";

    public static TemaInfoResponseDto empty() {
        return TemaInfoResponseDto.builder()
                .temas(new ArrayList<>())
                .totalNodes(0)
                .version("1.0")
                .build();
    }

    /**
     * УЗЕЛ ТЕМЫ (ТОЛЬКО информация, БЕЗ изделий)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemaInfoNode {
        private Integer id;
        private String name;
        private Integer indeksKd;
        // НЕТ izdelia - только информация о теме
    }
}