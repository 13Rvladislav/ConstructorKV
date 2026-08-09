package com.example.constructorKV.common.dto.external;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"entityName", "displayName", "description", "fields"})
public class ExternalEntityDto {
    private String entityName;
    private String displayName;
    private String description;
    private List<ExternalFieldDto> fields;
}