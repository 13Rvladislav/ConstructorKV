package com.example.constructorKV.kvtable.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KvTableRequestDto {
    private Integer temaId;
    private Integer izdelieId;
    private Integer groupeId;
}