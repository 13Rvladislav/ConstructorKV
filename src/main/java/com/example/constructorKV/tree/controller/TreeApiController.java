package com.example.constructorKV.tree.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.tree.dto.FullTreeResponseDto;
import com.example.constructorKV.tree.dto.TemaInfoResponseDto;
import com.example.constructorKV.tree.service.TreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tree")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Дерево навигации API",
        description = "JSON API для дерева Темы → Изделия → Группы")
public class TreeApiController {

    private final TreeService treeService;

    // ========================================================================
    // GET /api/tree/full - ПОЛНОЕ ДЕРЕВО
    // ========================================================================

    @GetMapping("/full")
    @Operation(
            summary = "Полное дерево",
            description = "Возвращает полное дерево: Темы → Изделия → Группы"
    )
    public ApiResponse<FullTreeResponseDto> getFullTree() {
        log.info("📥 GET /api/tree/full - запрос полного дерева");

        try {
            FullTreeResponseDto result = treeService.buildFullTree();
            return ApiResponse.success(result, "Дерево успешно построено");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    // ========================================================================
    // GET /api/tree/tema/{temaId} - ДЕРЕВО ДЛЯ КОНКРЕТНОЙ ТЕМЫ
    // ========================================================================

    @GetMapping("/tema/{temaId}")
    @Operation(
            summary = "Дерево для конкретной темы",
            description = "Возвращает дерево для указанной темы: Тема → Изделия → Группы"
    )
    public ApiResponse<FullTreeResponseDto> getTreeByTemaId(
            @Parameter(description = "ID темы", example = "4")
            @PathVariable Integer temaId) {

        log.info("📥 GET /api/tree/tema/{} - запрос дерева для темы", temaId);

        try {
            FullTreeResponseDto result = treeService.buildTreeByTemaId(temaId);
            return ApiResponse.success(result, "Дерево для темы успешно построено");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    // ========================================================================
    // GET /api/tree/tema/{temaId}/info - ИНФОРМАЦИЯ О ТЕМЕ (БЕЗ ИЗДЕЛИЙ)
    // ========================================================================

    @GetMapping("/tema/{temaId}/info")
    @Operation(
            summary = "Информация о теме",
            description = "Возвращает ТОЛЬКО информацию о теме, БЕЗ вложенных изделий"
    )
    public ApiResponse<TemaInfoResponseDto> getTemaInfo(
            @Parameter(description = "ID темы", example = "4")
            @PathVariable Long temaId) {

        log.info("📥 GET /api/tree/tema/{}/info - запрос информации о теме", temaId);

        try {
            TemaInfoResponseDto result = treeService.getTemaInfo(temaId);
            return ApiResponse.success(result, "Информация о теме получена");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }



}