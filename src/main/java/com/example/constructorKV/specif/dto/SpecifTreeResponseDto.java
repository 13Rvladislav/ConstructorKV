package com.example.constructorKV.specif.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SpecifTreeResponseDto {

    /** Информация о группе */
    private GroupInfo groupInfo;

    /** Список корневых узлов */
    @Builder.Default
    private List<SpecifNodeDto> roots = new ArrayList<>();

    /** Общее количество узлов */
    private long totalNodes;

    @Builder.Default
    private String version = "1.0";

    public static SpecifTreeResponseDto empty() {
        return SpecifTreeResponseDto.builder()
                .roots(new ArrayList<>())
                .totalNodes(0)
                .version("1.0")
                .build();
    }

    @Data
    @Builder
    public static class GroupInfo {
        private Integer temaId;
        private String temaName;
        private Integer izdelieId;
        private String izdelieName;
        private Integer groupeId;
        private String groupeName;
    }
}