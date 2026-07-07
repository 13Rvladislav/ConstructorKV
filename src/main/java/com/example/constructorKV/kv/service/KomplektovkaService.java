// src/main/java/com/example/constructorKV/kv/service/KomplektovkaService.java
package com.example.constructorKV.kv.service;

import com.example.constructorKV.common.exception.BusinessException;
import com.example.constructorKV.kv.dto.AssemblyHierarchyDto;
import com.example.constructorKV.kv.dto.KomplektovkaDto;
import com.example.constructorKV.kv.dto.KVFormRequestDto;
import com.example.constructorKV.kv.entity.KomplektovkaEntity;
import com.example.constructorKV.kv.entity.ProductSeriesKomplektovkiEntity;
import com.example.constructorKV.kv.entity.StructureKomplektovkiEntity;
import com.example.constructorKV.kv.repository.KomplektovkaRepository;
import com.example.constructorKV.kv.repository.ProductSeriesKomplektovkiRepository;
import com.example.constructorKV.kv.repository.StructureKomplektovkiRepository;
import com.example.constructorKV.slzap.service.SlZapiskaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KomplektovkaService {

    private final KomplektovkaRepository komplektovkaRepository;
    private final StructureKomplektovkiRepository structureKomplektovkiRepository;
    private final ProductSeriesKomplektovkiRepository productSeriesKomplektovkiRepository;
    private final SlZapiskaService slZapiskaService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // ========================================================================
    // ПОЛУЧЕНИЕ КВ ПО СЛУЖЕБНОЙ ЗАПИСКЕ
    // ========================================================================

    public List<KomplektovkaDto> getKvBySlZapiskaId(Integer slZapiskaId) {
        log.info("🔍 Получение КВ для служебной записки {}", slZapiskaId);

        if (slZapiskaId == null) {
            throw new BusinessException("ID служебной записки не указан");
        }

        List<KomplektovkaEntity> kvList = komplektovkaRepository.findBySlZapiskaId(slZapiskaId);

        if (kvList.isEmpty()) {
            log.info("⚠️ Для служебной записки {} нет КВ", slZapiskaId);
            return new ArrayList<>();
        }

        List<KomplektovkaDto> result = new ArrayList<>();
        for (KomplektovkaEntity kv : kvList) {
            result.add(convertToDto(kv));
        }

        log.info("✅ Найдено {} КВ для служебной записки {}", result.size(), slZapiskaId);
        return result;
    }

    // ========================================================================
    // ФОРМИРОВАНИЕ НОВОЙ КВ
    // ========================================================================

    @Transactional
    public KomplektovkaDto formKV(KVFormRequestDto request) {
        Integer slZapiskaId = request.getSlZapiskaId();
        Integer specifId = request.getSpecifId();

        log.info("📝 Формирование КВ для СЗ {} и сборки {}", slZapiskaId, specifId);

        // ===== ПРОВЕРКА 1: СЗ должна быть согласована =====
        if (!slZapiskaService.isSlZapiskaAgreed(slZapiskaId)) {
            log.warn("⚠️ Служебная записка {} не согласована", slZapiskaId);
            throw new BusinessException("❌ Служебная записка не согласована! КВ можно формировать только для согласованных СЗ.");
        }

        // ===== ПРОВЕРКА 2: Сборка должна принадлежать СЗ =====
        List<com.example.constructorKV.slzap.dto.AssemblyForKVDto> assemblies =
                slZapiskaService.getAssembliesForSlZap(slZapiskaId);
        boolean valid = assemblies.stream().anyMatch(a -> a.getSpecifId().equals(specifId));
        if (!valid) {
            log.warn("⚠️ Сборка {} не найдена в СЗ {}", specifId, slZapiskaId);
            throw new BusinessException("❌ Сборка с ID " + specifId + " не найдена в служебной записке");
        }

        // ===== ПРОВЕРКА 3: На эту сборку уже есть КВ? =====
        List<Integer> existingKvSpecifIds = komplektovkaRepository.findExistingSpecifIdsBySlZap(slZapiskaId);
        if (existingKvSpecifIds.contains(specifId)) {
            log.warn("⚠️ На сборку {} уже есть КВ", specifId);
            throw new BusinessException("❌ На эту сборку уже есть КВ! Повторное формирование невозможно.");
        }

        // ===== ШАГ 4: Создаём запись в KOMPLECTOVKA =====
        KomplektovkaEntity kv = new KomplektovkaEntity();
        kv.setIdSz(slZapiskaId);
        kv.setDesign("KV-" + slZapiskaId + "-" + System.currentTimeMillis());
        kv.setSborkiSz(String.valueOf(specifId));
        kv.setDateCreate(LocalDateTime.now());
        kv.setStatus((short) 0);  // 0 - активна

        kv = komplektovkaRepository.save(kv);
        log.info("✅ КВ создана с ID: {}", kv.getId());

        // ===== ШАГ 5: Получение иерархии сборок =====
        log.info("📊 Получение иерархии сборок для спецификации {}", specifId);
        List<AssemblyHierarchyDto> hierarchy = slZapiskaService.getAssemblyHierarchy(specifId);

        if (hierarchy != null && !hierarchy.isEmpty()) {
            // ===== ШАГ 6: Сохраняем структуру КВ (только подходящие узлы) =====
            log.info("💾 Сохранение структуры КВ для KV ID: {}", kv.getId());
            int savedCount = saveStructure(kv.getId(), hierarchy, specifId);
            log.info("✅ Сохранено {} элементов структуры для КВ {}", savedCount, kv.getId());
        } else {
            log.warn("⚠️ Для спецификации {} нет структуры", specifId);
        }

        log.info("✅ КВ успешно сформирована. ID: {}", kv.getId());
        return convertToDto(kv);
    }

    // ========================================================================
    // СОХРАНЕНИЕ СТРУКТУРЫ КВ
    // ========================================================================

    /**
     * СОХРАНИТЬ СТРУКТУРУ КВ В БД
     * Сохраняются только узлы, прошедшие проверку по сериям (fitsSeries = true)
     *
     * @param kvId ID КВ
     * @param hierarchy иерархия сборок
     * @param rootSpecifId ID корневой спецификации
     * @return количество сохранённых узлов
     */
    @Transactional
    public int saveStructure(Integer kvId, List<AssemblyHierarchyDto> hierarchy, Integer rootSpecifId) {
        log.info("💾 Сохранение структуры для КВ {}, корень: {}", kvId, rootSpecifId);

        // Очищаем старую структуру (если была)
        structureKomplektovkiRepository.deleteByKomplektovkaId(kvId);
        productSeriesKomplektovkiRepository.deleteByKomplektovkaId(kvId);

        int savedCount = 0;

        // Сохраняем только подходящие узлы
        for (AssemblyHierarchyDto node : hierarchy) {
            savedCount += saveNodeRecursive(kvId, node, null, rootSpecifId);
        }

        log.info("✅ Сохранено {} элементов структуры для КВ {}", savedCount, kvId);
        return savedCount;
    }

    /**
     * РЕКУРСИВНОЕ СОХРАНЕНИЕ УЗЛА И ЕГО ДЕТЕЙ
     * Сохраняются только узлы, прошедшие проверку по сериям (fitsSeries = true)
     *
     * @param kvId ID КВ
     * @param node текущий узел
     * @param parentId ID родителя в STRUCTURA_KOMPLECTOVKI
     * @param rootSpecifId ID корневой спецификации
     * @return количество сохранённых узлов (включая потомков)
     */
    private int saveNodeRecursive(Integer kvId, AssemblyHierarchyDto node, Integer parentId, Integer rootSpecifId) {
        int count = 0;

        // Пропускаем узлы, которые не прошли проверку по сериям
        // fitsSeries = false — не сохраняем
        // fitsSeries = null или true — сохраняем
        if (node.getFitsSeries() != null && !node.getFitsSeries()) {
            log.debug("⏭️ Пропущен узел {} (не подходит по сериям)", node.getObozn());
            return 0;
        }

        // Создаём запись в STRUCTURA_KOMPLECTOVKI
        StructureKomplektovkiEntity entity = new StructureKomplektovkiEntity();
        entity.setIdKomplektovka(kvId);
        entity.setIdSpecif(node.getId());
        entity.setDesign(node.getObozn());
        entity.setName(node.getNaim());
        entity.setQuantity(node.getQuantity() != null ? node.getQuantity() : 1);
        entity.setMassa(node.getMassa() != null ? node.getMassa() : 0.0);
        entity.setIdDivision(3);  // ID_DIVISION = 3 (сборки)
        entity.setParent(parentId);
        entity.setLevel(node.getLevel() != null ? node.getLevel() : 0);
        entity.setIdRootSpecif(rootSpecifId);
        entity.setIdOriginalStructure(node.getStructureId() != null ? node.getStructureId() : 0);

        // Сохраняем и получаем ID
        entity = structureKomplektovkiRepository.save(entity);
        log.debug("✅ Сохранён узел: {} (ID: {}, уровень: {})", node.getObozn(), entity.getId(), entity.getLevel());
        count++;

        // Сохраняем серии для этого узла
        if (node.getSeries() != null && !node.getSeries().isEmpty()) {
            for (AssemblyHierarchyDto.SeriesInfo seriesInfo : node.getSeries()) {
                ProductSeriesKomplektovkiEntity series = new ProductSeriesKomplektovkiEntity();
                series.setSsid(entity.getId());
                series.setMini(seriesInfo.getMini());
                series.setMaxi(seriesInfo.getMaxi());
                series.setQuantity(seriesInfo.getQuantity());
                series.setMassa(seriesInfo.getMassa());
                series.setDimensions(seriesInfo.getDimensions());
                productSeriesKomplektovkiRepository.save(series);
                log.debug("  🔢 Сохранена серия: {}-{}", seriesInfo.getMini(), seriesInfo.getMaxi());
            }
        }

        // Рекурсивно сохраняем детей (только если они подходят)
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (AssemblyHierarchyDto child : node.getChildren()) {
                count += saveNodeRecursive(kvId, child, entity.getId(), rootSpecifId);
            }
        }

        return count;
    }

    // ========================================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ========================================================================

    private KomplektovkaDto convertToDto(KomplektovkaEntity entity) {
        String dateStr = entity.getDateCreate() != null
                ? entity.getDateCreate().format(DATE_FORMATTER)
                : null;

        String status = determineStatus(entity);

        return KomplektovkaDto.builder()
                .id(entity.getId())
                .design(entity.getDesign())
                .idSz(entity.getIdSz())
                .sborkiSz(entity.getSborkiSz())
                .dateCreate(dateStr)
                .status(status)
                .build();
    }

    private String determineStatus(KomplektovkaEntity entity) {
        if (entity.getDateArchiving() != null) {
            return "🗑️ Заархивирована";
        }
        if (entity.getDateCreate() == null) {
            return "⏳ Не сформирована";
        }
        return "✅ Активна";
    }
}