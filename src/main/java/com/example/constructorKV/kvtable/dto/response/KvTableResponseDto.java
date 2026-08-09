package com.example.constructorKV.kvtable.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class KvTableResponseDto {

    private GroupInfo groupInfo;

    @Builder.Default
    private List<KvTableRowDto> rows = new ArrayList<>();

    private long totalRows;

    @Builder.Default
    private String version = "1.0";

    public static KvTableResponseDto empty() {
        return KvTableResponseDto.builder()
                .rows(new ArrayList<>())
                .totalRows(0)
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
    }
}