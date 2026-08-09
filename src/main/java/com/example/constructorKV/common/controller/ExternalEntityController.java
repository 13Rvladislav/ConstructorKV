package com.example.constructorKV.common.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.common.dto.external.ExternalEntitiesResponseDto;
import com.example.constructorKV.common.dto.external.ExternalEntityDto;
import com.example.constructorKV.common.service.ExternalEntityService;
import com.example.constructorKV.kv.dto.KomplektovkaDto;
import com.example.constructorKV.kvtable.dto.response.KvTableRowDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/external/entities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Внешние сущности", description = "API для регистрации сущностей из внешних систем")
public class ExternalEntityController {

    private final ExternalEntityService externalEntityService;

    @GetMapping("/kvtablerow/metadata")
    @Operation(summary = "Получить метаданные сущности KvTableRowDto")
    public ApiResponse<ExternalEntityDto> getKvTableRowMetadata() {
        log.info("📥 Запрос метаданных KvTableRowDto");

        try {
            ExternalEntityDto entity = externalEntityService.buildEntityMetadata(KvTableRowDto.class);
            return ApiResponse.success(entity, "Метаданные KvTableRow получены");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/metadata/all")
    @Operation(summary = "Получить метаданные всех зарегистрированных сущностей")
    public ApiResponse<ExternalEntitiesResponseDto> getAllEntitiesMetadata() {
        log.info("📥 Запрос метаданных всех сущностей");

        try {
            List<ExternalEntityDto> entities = new ArrayList<>();

            entities.add(externalEntityService.buildEntityMetadata(KvTableRowDto.class));
            entities.add(externalEntityService.buildEntityMetadata(KomplektovkaDto.class));

            log.info("✅ Возвращено {} сущностей", entities.size());

            ExternalEntitiesResponseDto response = ExternalEntitiesResponseDto.builder()
                    .source("Java.ConstructorKV")
                    .totalCount(entities.size())
                    .entities(entities)
                    .build();

            return ApiResponse.success(response, "Метаданные всех сущностей получены");

        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }
}