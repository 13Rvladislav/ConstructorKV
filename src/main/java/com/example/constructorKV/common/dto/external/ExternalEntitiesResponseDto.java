package com.example.constructorKV.common.dto.external;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"source", "totalCount", "entities"})
public class ExternalEntitiesResponseDto {
    private String source;              // Источник системы (Java.ConstructorKV)
    private int totalCount;             // Общее количество сущностей
    private List<ExternalEntityDto> entities;   // Список сущностей
}