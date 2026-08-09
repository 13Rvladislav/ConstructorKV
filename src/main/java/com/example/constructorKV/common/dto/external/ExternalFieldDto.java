package com.example.constructorKV.common.dto.external;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({
        "name",
        "displayName",
        "type",
        "isRequired",
        "isKey",
        "isNavigation",
        "relatedEntity",
        "category",
        "displayOrder"
})
public class ExternalFieldDto {
    private String name;
    private String displayName;
    private String type;
    private Boolean isRequired;
    private Boolean isKey;
    private Boolean isNavigation;
    private String relatedEntity;
    private String category;
    private Integer displayOrder;
}