package com.example.constructorKV.slzap.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.slzap.dto.AssemblyForKVDto;
import com.example.constructorKV.slzap.dto.SlZapiskaDto;
import com.example.constructorKV.slzap.service.SlZapiskaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slzap")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Служебные записки", description = "API для работы со служебными записками")
public class SlZapiskaController {

    private final SlZapiskaService slZapiskaService;

    @GetMapping("/by-specif/{specifId}")
    @Operation(
            summary = "Служебные записки для спецификации",
            description = "Возвращает список служебных записок, связанных с указанной спецификацией"
    )
    public ApiResponse<List<SlZapiskaDto>> getSlZapBySpecifId(
            @Parameter(description = "ID спецификации", example = "3")
            @PathVariable Integer specifId) {

        log.info("📥 GET /api/slzap/by-specif/{} - запрос служебных записок", specifId);

        try {
            List<SlZapiskaDto> result = slZapiskaService.getSlZapBySpecifId(specifId);
            return ApiResponse.success(result, "Служебные записки получены");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(
            summary = "Все служебные записки",
            description = "Возвращает список всех служебных записок"
    )
    public ApiResponse<List<SlZapiskaDto>> getAllSlZap() {
        log.info("📥 GET /api/slzap/all - запрос всех служебных записок");

        try {
            List<SlZapiskaDto> result = slZapiskaService.getAllSlZap();
            return ApiResponse.success(result, "Служебные записки получены");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Информация о служебной записке",
            description = "Возвращает информацию о служебной записке по ID"
    )
    public ApiResponse<SlZapiskaDto> getSlZapById(
            @Parameter(description = "ID служебной записки", example = "1")
            @PathVariable Integer id) {

        log.info("📥 GET /api/slzap/{} - запрос информации о служебной записке", id);

        try {
            SlZapiskaDto result = slZapiskaService.getSlZapById(id);
            return ApiResponse.success(result, "Информация о служебной записке получена");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    /**
     * ПОЛУЧИТЬ СПИСОК СБОРОК ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ
     *
     * Пример: GET /api/slzap/11/assemblies
     *
     * @param slZapiskaId ID служебной записки
     * @return список сборок
     */
    @GetMapping("/{slZapiskaId}/assemblies")
    @Operation(
            summary = "Сборки для служебной записки",
            description = "Возвращает список сборок (спецификаций), доступных для формирования КВ"
    )
    public ApiResponse<List<AssemblyForKVDto>> getAssembliesForSlZap(
            @Parameter(description = "ID служебной записки", example = "11")
            @PathVariable Integer slZapiskaId) {

        log.info("📥 GET /api/slzap/{}/assemblies - запрос сборок для СЗ", slZapiskaId);

        try {
            List<AssemblyForKVDto> result = slZapiskaService.getAssembliesForSlZap(slZapiskaId);
            return ApiResponse.success(result, "Сборки получены");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }
}