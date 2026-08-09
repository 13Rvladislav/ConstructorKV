package com.example.constructorKV.kv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KVFormRequestDto {

    private Integer slZapiskaId;
    private Integer specifId;
    private Integer formedBy;
    private Integer parentKvId;
}