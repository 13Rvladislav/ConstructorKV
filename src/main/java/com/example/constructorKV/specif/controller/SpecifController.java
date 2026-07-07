package com.example.constructorKV.specif.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.specif.dto.SpecifTreeResponseDto;
import com.example.constructorKV.specif.service.SpecifService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * API КОНТРОЛЛЕР ДЛЯ СПЕЦИФИКАЦИЙ
 *
 * Блок "Спецификации".
 *
 * Вход: тема → изделие → группа (из первого дерева)
 * Выход: дерево спецификаций с учётом LINK_IMPLEMETATION
 *
 * @RestController - контроллер с REST-методами
 * @RequestMapping - базовый путь /api/specif
 * @RequiredArgsConstructor - конструктор для final полей
 * @Slf4j - логирование
 */
@RestController
@RequestMapping("/api/specif")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Спецификации API",
        description = "API для работы с деревом спецификаций")
public class SpecifController {

    private final SpecifService specifService;

    // ========================================================================
    // GET /api/specif/tree - ДЕРЕВО СПЕЦИФИКАЦИЙ ПО ГРУППЕ
    // ========================================================================

    /**
     * ПОЛУЧЕНИЕ ДЕРЕВА СПЕЦИФИКАЦИЙ ПО ГРУППЕ
     *
     * На входе: ID темы, ID изделия, ID группы (из первого дерева)
     * На выходе: полное дерево спецификаций с учётом LINK_IMPLEMETATION
     *
     * Пример запроса:
     * GET /api/specif/tree?temaId=4&izdelieId=235&groupeId=1
     *
     * @param temaId ID темы (из первого дерева)
     * @param izdelieId ID изделия (из первого дерева)
     * @param groupeId ID группы (из первого дерева)
     * @return ApiResponse с деревом спецификаций
     */
    @GetMapping("/tree")
    @Operation(
            summary = "Дерево спецификаций по группе",
            description = """
                    Возвращает дерево спецификаций для указанной группы.
                    
                    На входе: temaId, izdelieId, groupeId (из первого дерева)
                    На выходе: полное дерево спецификаций с учётом LINK_IMPLEMETATION
                    
                    LINK_IMPLEMETATION означает, что спецификация заимствует данные 
                    из другой спецификации. При обходе дерева мы подставляем данные 
                    из оригинальной спецификации.
                    """
    )
    public ApiResponse<SpecifTreeResponseDto> getSpecifTree(
            @Parameter(description = "ID темы из первого дерева", example = "4")
            @RequestParam Integer temaId,

            @Parameter(description = "ID изделия из первого дерева", example = "235")
            @RequestParam Integer izdelieId,

            @Parameter(description = "ID группы из первого дерева", example = "1")
            @RequestParam Integer groupeId) {

        log.info("📥 GET /api/specif/tree - тема: {}, изделие: {}, группа: {}",
                temaId, izdelieId, groupeId);

        try {
            SpecifTreeResponseDto result = specifService.buildSpecifTree(temaId, izdelieId, groupeId);
            return ApiResponse.success(result, "Дерево спецификаций построено");
        } catch (Exception e) {
            log.error("❌ Ошибка при построении дерева спецификаций: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }
}