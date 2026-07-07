// src/main/java/com/example/constructorKV/kv/controller/KomplektovkaController.java
package com.example.constructorKV.kv.controller;

import com.example.constructorKV.common.dto.ApiResponse;
import com.example.constructorKV.kv.dto.AssemblyHierarchyDto;
import com.example.constructorKV.kv.dto.KomplektovkaDto;
import com.example.constructorKV.kv.dto.KVFormRequestDto;
import com.example.constructorKV.kv.service.KomplektovkaService;
import com.example.constructorKV.slzap.service.SlZapiskaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kv")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Комплектовочные ведомости", description = "API для работы с КВ")
public class KomplektovkaController {

    private final KomplektovkaService komplektovkaService;
    private final SlZapiskaService slZapiskaService;

    // ========================================================================
    // GET /api/kv/by-slzap/{slZapiskaId}
    // ========================================================================

    @GetMapping("/by-slzap/{slZapiskaId}")
    @Operation(
            summary = "КВ по служебной записке",
            description = "Возвращает список всех КВ для указанной служебной записки"
    )
    public ApiResponse<List<KomplektovkaDto>> getKvBySlZapiska(
            @Parameter(description = "ID служебной записки", example = "11")
            @PathVariable Integer slZapiskaId) {

        log.info("📥 GET /api/kv/by-slzap/{} - запрос КВ по СЗ", slZapiskaId);

        try {
            List<KomplektovkaDto> result = komplektovkaService.getKvBySlZapiskaId(slZapiskaId);
            return ApiResponse.success(result, "КВ получены");
        } catch (Exception e) {
            log.error("❌ Ошибка при получении КВ: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    // ========================================================================
    // POST /api/kv/form
    // ========================================================================

    @PostMapping("/form")
    @Operation(
            summary = "Сформировать КВ",
            description = """
                    Создаёт новую КВ для указанной служебной записки и сборки.
                    
                    Проверки:
                    - СЗ должна быть согласована
                    - Сборка должна принадлежать СЗ
                    - На сборку не должно быть существующей КВ
                    """
    )
    public ApiResponse<KomplektovkaDto> formKV(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры формирования КВ"
            )
            @RequestBody KVFormRequestDto request) {

        log.info("📥 POST /api/kv/form - формирование КВ для СЗ {} и сборки {}",
                request.getSlZapiskaId(), request.getSpecifId());

        try {
            KomplektovkaDto result = komplektovkaService.formKV(request);
            log.info("✅ КВ успешно сформирована. ID: {}", result.getId());
            return ApiResponse.success(result, "КВ успешно сформирована");
        } catch (Exception e) {
            log.error("❌ Ошибка при формировании КВ: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }

    // ========================================================================
    // GET /api/kv/preview/assembly
    // ========================================================================

    @GetMapping("/preview/assembly")
    @Operation(
            summary = "Предпросмотр иерархии сборок",
            description = """
                    Возвращает дерево сборок для отображения перед формированием КВ.
                    
                    Каждый узел содержит:
                    - ID, обозначение, наименование, количество, масса
                    - Номер изделия (izdelieNumber) для фильтрации по сериям
                    - Список серий (mini, maxi, quantity, massa, dimensions)
                    - Статус соответствия (fitsSeries: true/false)
                    - Причину (fitReason) для отладки
                    """
    )
    public ApiResponse<List<AssemblyHierarchyDto>> previewAssemblyHierarchy(
            @Parameter(description = "ID сборки", example = "3")
            @RequestParam Integer specifId) {

        log.info("📥 GET /api/kv/preview/assembly?specifId={} - запрос иерархии", specifId);

        try {
            List<AssemblyHierarchyDto> result = slZapiskaService.getAssemblyHierarchy(specifId);
            return ApiResponse.success(result, "Иерархия сборок получена");
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            return ApiResponse.error("Ошибка: " + e.getMessage());
        }
    }
}