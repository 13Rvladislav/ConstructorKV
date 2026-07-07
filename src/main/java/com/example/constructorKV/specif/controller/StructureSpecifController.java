package com.example.constructorKV.specif.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.specif.dto.StructureSpecifDto;
import com.example.constructorKV.specif.service.StructureSpecifService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API КОНТРОЛЛЕР ДЛЯ СТРУКТУРЫ СПЕЦИФИКАЦИИ
 *
 * @RestController - контроллер с REST-методами
 * @RequestMapping - базовый путь /api/structure
 * @RequiredArgsConstructor - конструктор для final полей
 * @Slf4j - логирование
 */
@RestController
@RequestMapping("/api/structure")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Структура спецификации", description = "API для работы со структурой спецификации")
public class StructureSpecifController {

    private final StructureSpecifService structureSpecifService;

    /**
     * ПОЛУЧИТЬ СТРУКТУРУ СПЕЦИФИКАЦИИ ПО ID
     *
     * Пример: GET /api/structure/3
     *
     * @param specifId ID спецификации
     * @return список элементов структуры
     */
    @GetMapping("/{specifId}")
    @Operation(
            summary = "Структура спецификации",
            description = "Возвращает структуру указанной спецификации (детали, сборки, материалы и т.д.)"
    )
    public ApiResponse<List<StructureSpecifDto>> getStructure(
            @Parameter(description = "ID спецификации", example = "3")
            @PathVariable Integer specifId) {

        log.info("📥 GET /api/structure/{} - запрос структуры спецификации", specifId);

        try {
            List<StructureSpecifDto> result = structureSpecifService.getStructureBySpecifId(specifId);
            return ApiResponse.success(result, "Структура получена");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }
}