package com.example.constructorKV.kv.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KvStatusDto {
    private Integer id;
    private String name;
    private String description;
}