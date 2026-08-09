package com.example.constructorKV.kvtable.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.kvtable.dto.request.KvTableRequestDto;
import com.example.constructorKV.kvtable.dto.response.KvTableResponseDto;
import com.example.constructorKV.kvtable.service.KvTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kvtable")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Таблица КВ", description = "API для получения сводной таблицы комплектовочных ведомостей")
public class KvTableController {

    private final KvTableService kvTableService;

    @GetMapping
    @Operation(
            summary = "Получить таблицу КВ",
            description = "Возвращает сводную таблицу комплектовочных ведомостей"
    )
    public ApiResponse<KvTableResponseDto> getKvTable(
            @Parameter(description = "ID темы", example = "4") @RequestParam Integer temaId,
            @Parameter(description = "ID изделия", example = "235") @RequestParam Integer izdelieId,
            @Parameter(description = "ID группы", example = "1") @RequestParam Integer groupeId) {

        log.info("📥 GET /api/kvtable - тема: {}, изделие: {}, группа: {}",
                temaId, izdelieId, groupeId);

        try {
            KvTableRequestDto request = KvTableRequestDto.builder()
                    .temaId(temaId)
                    .izdelieId(izdelieId)
                    .groupeId(groupeId)
                    .build();

            KvTableResponseDto result = kvTableService.getKvTable(request);
            return ApiResponse.success(result, "Таблица КВ получена");

        } catch (Exception e) {
            log.error("❌ Ошибка при получении таблицы КВ: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }
}