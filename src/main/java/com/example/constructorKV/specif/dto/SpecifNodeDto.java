package com.example.constructorKV.specif.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SpecifNodeDto {
    private Integer id;          // оригинальный ID из SPECIF22
    private Integer displayId;   // ID для отображения (с учетом LINK)
    private Integer parentId;
    private String obozn;
    private String naim;
    private Double massa;
    private String letters;
    private Short flag;
    private Integer level;
    @Builder.Default
    private Boolean isLinked = false;
    private String linkInfo;
    @Builder.Default
    private List<SpecifNodeDto> children = new ArrayList<>();
}