// src/main/java/com/example/constructorKV/slzap/service/SlZapiskaService.java
package com.example.constructorKV.slzap.service;

import com.example.constructorKV.common.exception.BusinessException;
import com.example.constructorKV.kv.dto.AssemblyHierarchyDto;
import com.example.constructorKV.kv.repository.KomplektovkaRepository;
import com.example.constructorKV.slzap.dto.AssemblyForKVDto;
import com.example.constructorKV.slzap.dto.SlZapiskaDto;
import com.example.constructorKV.slzap.entity.SlZapiskaEntity;
import com.example.constructorKV.slzap.repository.SlZapiskaRepository;
import com.example.constructorKV.specif.repository.SpecifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlZapiskaService {

    private final SlZapiskaRepository slZapiskaRepository;
    private final KomplektovkaRepository komplektovkaRepository;
    private final SpecifRepository specifRepository;

    // ========================================================================
    // ПОЛУЧЕНИЕ СЛУЖЕБНЫХ ЗАПИСОК ПО СПЕЦИФИКАЦИИ
    // ========================================================================

    public List<SlZapiskaDto> getSlZapBySpecifId(Integer specifId) {
        log.info("🔍 Поиск служебных записок для спецификации {}", specifId);

        if (specifId == null) {
            log.warn("⚠️ specifId = null");
            return new ArrayList<>();
        }

        try {
            List<Object[]> result = slZapiskaRepository.findSlZapBySpecifId(specifId);

            if (result == null || result.isEmpty()) {
                log.info("⚠️ Служебные записки для спецификации {} не найдены", specifId);
                return new ArrayList<>();
            }

            List<SlZapiskaDto> slZapList = new ArrayList<>();
            for (Object[] row : result) {
                Integer id = (Integer) row[0];
                String obozn = (String) row[1];
                String naimSlZap = (String) row[2];
                Short flagStatus = (Short) row[3];
                Integer izdelieId = (Integer) row[4];
                String izdelieName = (String) row[5];
                Integer izdelSlZapId = (Integer) row[6];

                boolean isAgreed = slZapiskaRepository.isSlZapiskaAgreed(id);

                SlZapiskaDto dto = SlZapiskaDto.builder()
                        .id(id)
                        .obozn(obozn != null ? obozn : "Без обозн.")
                        .naimSlZap(naimSlZap != null ? naimSlZap : "Без названия")
                        .status(getStatusText(flagStatus))
                        .isAgreed(isAgreed)
                        .agreedStatus(isAgreed ? "✅ СОГЛАСОВАНА" : "⏳ НЕ СОГЛАСОВАНА")
                        .izdelieId(izdelieId)
                        .izdelieName(izdelieName != null ? izdelieName : "Не указано")
                        .izdelSlZapId(izdelSlZapId)
                        .build();

                slZapList.add(dto);
            }

            log.info("✅ Найдено {} служебных записок для спецификации {}", slZapList.size(), specifId);
            return slZapList;

        } catch (Exception e) {
            log.error("❌ Ошибка при поиске служебных записок: {}", e.getMessage(), e);
            throw new BusinessException("Не удалось получить служебные записки: " + e.getMessage(), e);
        }
    }

    // ========================================================================
    // ПОЛУЧЕНИЕ ВСЕХ СЛУЖЕБНЫХ ЗАПИСОК
    // ========================================================================

    public List<SlZapiskaDto> getAllSlZap() {
        log.info("🔍 Получение всех служебных записок");

        try {
            List<SlZapiskaEntity> entities = slZapiskaRepository.findAllOrdered();

            if (entities == null || entities.isEmpty()) {
                log.info("⚠️ Служебные записки не найдены");
                return new ArrayList<>();
            }

            List<SlZapiskaDto> result = new ArrayList<>();
            for (SlZapiskaEntity e : entities) {
                boolean isAgreed = slZapiskaRepository.isSlZapiskaAgreed(e.getId());
                SlZapiskaDto dto = SlZapiskaDto.builder()
                        .id(e.getId())
                        .obozn(e.getObozn() != null ? e.getObozn() : "Без обозн.")
                        .naimSlZap(e.getNaimSlZap() != null ? e.getNaimSlZap() : "Без названия")
                        .status(getStatusText(e.getFlagStatus()))
                        .isAgreed(isAgreed)
                        .agreedStatus(isAgreed ? "✅ СОГЛАСОВАНА" : "⏳ НЕ СОГЛАСОВАНА")
                        .build();
                result.add(dto);
            }

            log.info("✅ Найдено {} служебных записок", result.size());
            return result;

        } catch (Exception e) {
            log.error("❌ Ошибка при получении служебных записок: {}", e.getMessage(), e);
            throw new BusinessException("Не удалось получить служебные записки: " + e.getMessage(), e);
        }
    }

    // ========================================================================
    // ПОЛУЧЕНИЕ СЛУЖЕБНОЙ ЗАПИСКИ ПО ID
    // ========================================================================

    public SlZapiskaDto getSlZapById(Integer id) {
        log.info("🔍 Поиск служебной записки по ID: {}", id);

        if (id == null) {
            throw new BusinessException("ID служебной записки не указан");
        }

        return slZapiskaRepository.findById(id)
                .map(e -> {
                    boolean isAgreed = slZapiskaRepository.isSlZapiskaAgreed(e.getId());
                    return SlZapiskaDto.builder()
                            .id(e.getId())
                            .obozn(e.getObozn() != null ? e.getObozn() : "Без обозн.")
                            .naimSlZap(e.getNaimSlZap() != null ? e.getNaimSlZap() : "Без названия")
                            .status(getStatusText(e.getFlagStatus()))
                            .isAgreed(isAgreed)
                            .agreedStatus(isAgreed ? "✅ СОГЛАСОВАНА" : "⏳ НЕ СОГЛАСОВАНА")
                            .build();
                })
                .orElseThrow(() -> new BusinessException("Служебная записка с ID " + id + " не найдена"));
    }

    // ========================================================================
    // ПРОВЕРКА СОГЛАСОВАНИЯ
    // ========================================================================

    public boolean isSlZapiskaAgreed(Integer slZapiskaId) {
        if (slZapiskaId == null) {
            return false;
        }
        return slZapiskaRepository.isSlZapiskaAgreed(slZapiskaId);
    }

    // ========================================================================
    // ПОЛУЧЕНИЕ СБОРОК ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ
    // ========================================================================

    public List<AssemblyForKVDto> getAssembliesForSlZap(Integer slZapiskaId) {
        log.info("🔍 Получение сборок для служебной записки {}", slZapiskaId);

        if (slZapiskaId == null) {
            throw new BusinessException("ID служебной записки не указан");
        }

        if (!slZapiskaRepository.existsById(slZapiskaId)) {
            throw new BusinessException("Служебная записка с ID " + slZapiskaId + " не найдена");
        }

        List<Object[]> result = slZapiskaRepository.findAssembliesForSlZap(slZapiskaId);

        if (result == null || result.isEmpty()) {
            log.info("⚠️ Для служебной записки {} нет сборок", slZapiskaId);
            return new ArrayList<>();
        }

        // Получаем список уже существующих КВ для этой СЗ
        List<Integer> existingKvSpecifIds = komplektovkaRepository.findExistingSpecifIdsBySlZap(slZapiskaId);

        List<AssemblyForKVDto> assemblies = new ArrayList<>();
        for (Object[] row : result) {
            Integer specifId = (Integer) row[0];
            String obozn = (String) row[1];
            String izdelieName = (String) row[2];

            boolean hasKv = existingKvSpecifIds.contains(specifId);

            AssemblyForKVDto dto = AssemblyForKVDto.builder()
                    .specifId(specifId)
                    .obozn(obozn != null ? obozn : "Без обозн.")
                    .izdelieName(izdelieName != null ? izdelieName : "Не указано")
                    .hasKv(hasKv)
                    .build();

            assemblies.add(dto);
        }

        log.info("✅ Найдено {} сборок для служебной записки {}", assemblies.size(), slZapiskaId);
        return assemblies;
    }

    // ========================================================================
    // ПОЛУЧЕНИЕ ИЕРАРХИИ СБОРОК ДЛЯ ПРЕДПРОСМОТРА
    // ========================================================================

    /**
     * ПОЛУЧИТЬ ИЕРАРХИЮ СБОРОК ДЛЯ СПЕЦИФИКАЦИИ
     *
     * Используется для предпросмотра структуры сборок перед формированием КВ.
     * Возвращает дерево сборок с учётом LINK_IMPLEMETATION.
     * Каждый узел помечен флагом fitsSeries (подходит ли под серии).
     *
     * @param specifId ID корневой спецификации
     * @return дерево сборок
     */
    public List<AssemblyHierarchyDto> getAssemblyHierarchy(Integer specifId) {
        log.info("🔍 Получение иерархии сборок для спецификации {}", specifId);

        if (specifId == null) {
            throw new BusinessException("ID спецификации не указан");
        }

        if (!specifRepository.existsById(specifId)) {
            throw new BusinessException("Спецификация с ID " + specifId + " не найдена");
        }

        List<Object[]> result = slZapiskaRepository.findAssemblyHierarchy(specifId);

        if (result == null || result.isEmpty()) {
            log.info("⚠️ Для спецификации {} нет дочерних сборок", specifId);
            return List.of(createRootNode(specifId));
        }

        // Строим дерево из плоского списка
        Map<Integer, AssemblyHierarchyDto> nodeMap = new LinkedHashMap<>();
        List<AssemblyHierarchyDto> roots = new ArrayList<>();

        for (Object[] row : result) {
            Integer id = (Integer) row[0];
            Integer parentId = (Integer) row[1];
            String obozn = (String) row[2];
            String naim = (String) row[3];
            Integer quantity = (Integer) row[4];
            Double massa = (Double) row[5];
            Integer level = (Integer) row[6];
            Integer structureId = (Integer) row[8];
            Integer izdelieNumber = (Integer) row[9];
            Integer izdelieId = (Integer) row[10];

            AssemblyHierarchyDto node = AssemblyHierarchyDto.builder()
                    .id(id)
                    .obozn(obozn != null ? obozn : "Без обозн.")
                    .naim(naim != null ? naim : "Без названия")
                    .quantity(quantity != null ? quantity : 1)
                    .massa(massa != null ? massa : 0.0)
                    .parentId(parentId)
                    .level(level != null ? level : 0)
                    .structureId(structureId)
                    .izdelieNumber(izdelieNumber)
                    .izdelieId(izdelieId)
                    .fitsSeries(null)
                    .fitReason(null)
                    .series(new ArrayList<>())
                    .children(new ArrayList<>())
                    .build();

            nodeMap.put(id, node);

            if (parentId == null) {
                roots.add(node);
            }
        }

        // Строим связи родитель-ребёнок
        for (Map.Entry<Integer, AssemblyHierarchyDto> entry : nodeMap.entrySet()) {
            AssemblyHierarchyDto node = entry.getValue();
            if (node.getParentId() != null) {
                AssemblyHierarchyDto parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.addChild(node);
                }
            }
        }

        // Загружаем серии
        loadSeriesForHierarchy(roots);

        // Сортируем детей
        for (AssemblyHierarchyDto root : roots) {
            sortChildren(root);
        }

        // Маркировка по сериям (с учётом родительских связей)
        markBySeries(roots);

        if (roots.isEmpty() && !nodeMap.isEmpty()) {
            AssemblyHierarchyDto root = nodeMap.values().stream()
                    .min(Comparator.comparing(AssemblyHierarchyDto::getLevel))
                    .orElse(null);
            if (root != null) {
                roots.add(root);
            }
        }

        log.info("✅ Построена иерархия сборок: {} корневых элементов, всего {} узлов",
                roots.size(), countAllNodes(roots));
        return roots;
    }

    /**
     * МАРКИРОВКА УЗЛОВ ПО СЕРИЯМ
     *
     * Важно: если родитель не прошёл по серийности,
     * все его дети автоматически помечаются как неподходящие,
     * даже если их собственные серии подходят.
     */
    private void markBySeries(List<AssemblyHierarchyDto> nodes) {
        for (AssemblyHierarchyDto node : nodes) {
            // Проверяем, подходит ли текущий узел по сериям
            Integer productNumber = node.getIzdelieNumber();
            boolean nodeFits = node.checkFitsSeries(productNumber);

            // Если узел НЕ ПОДХОДИТ → все дети автоматически неподходящие
            if (!nodeFits) {
                markChildrenAsNonFitting(node);
            } else {
                // Если узел ПОДХОДИТ → проверяем детей независимо
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    markBySeries(node.getChildren());
                }
            }
        }
    }

    /**
     * ПОМЕЧАЕТ ВСЕХ ДОЧЕРНИХ УЗЛОВ КАК НЕПОДХОДЯЩИЕ
     * (рекурсивно по всему поддереву)
     */
    private void markChildrenAsNonFitting(AssemblyHierarchyDto node) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (AssemblyHierarchyDto child : node.getChildren()) {
                child.setFitsSeries(false);
                child.setFitReason("Родительский узел не прошёл по серийности");
                // Рекурсивно помечаем всех потомков
                markChildrenAsNonFitting(child);
            }
        }
    }

    /**
     * ЗАГРУЗИТЬ СЕРИИ ДЛЯ ВСЕХ ЭЛЕМЕНТОВ ИЕРАРХИИ
     */
    private void loadSeriesForHierarchy(List<AssemblyHierarchyDto> hierarchy) {
        if (hierarchy == null || hierarchy.isEmpty()) {
            return;
        }

        for (AssemblyHierarchyDto node : hierarchy) {
            // Загружаем серии для текущего узла
            if (node.getStructureId() != null) {
                List<Object[]> seriesData = slZapiskaRepository.findSeriesByStructureId(node.getStructureId());
                if (seriesData != null && !seriesData.isEmpty()) {
                    for (Object[] row : seriesData) {
                        Integer mini = (Integer) row[0];
                        Integer maxi = (Integer) row[1];
                        Integer quantity = (Integer) row[2];
                        Double massa = (Double) row[3];
                        String dimensions = (String) row[4];

                        AssemblyHierarchyDto.SeriesInfo series = AssemblyHierarchyDto.SeriesInfo.builder()
                                .mini(mini)
                                .maxi(maxi)
                                .quantity(quantity)
                                .massa(massa)
                                .dimensions(dimensions)
                                .build();
                        node.addSeries(series);
                    }
                }
            }

            // Рекурсивно обрабатываем детей
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                loadSeriesForHierarchy(node.getChildren());
            }
        }
    }

    /**
     * СОЗДАТЬ КОРНЕВОЙ УЗЕЛ (если нет детей)
     */
    private AssemblyHierarchyDto createRootNode(Integer specifId) {
        return specifRepository.findById(specifId)
                .map(s -> AssemblyHierarchyDto.builder()
                        .id(s.getId())
                        .obozn(s.getObozn() != null ? s.getObozn() : "Без обозн.")
                        .naim(s.getNaim() != null ? s.getNaim() : "Без названия")
                        .quantity(1)
                        .massa(s.getMassa() != null ? s.getMassa() : 0.0)
                        .parentId(null)
                        .level(0)
                        .structureId(null)
                        .izdelieNumber(null)
                        .izdelieId(null)
                        .fitsSeries(true)
                        .fitReason("Корневая сборка")
                        .series(new ArrayList<>())
                        .children(new ArrayList<>())
                        .build())
                .orElseThrow(() -> new BusinessException("Спецификация с ID " + specifId + " не найдена"));
    }

    /**
     * РЕКУРСИВНАЯ СОРТИРОВКА ДЕТЕЙ
     */
    private void sortChildren(AssemblyHierarchyDto node) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            node.getChildren().sort(Comparator.comparing(
                    AssemblyHierarchyDto::getObozn,
                    String.CASE_INSENSITIVE_ORDER
            ));
            for (AssemblyHierarchyDto child : node.getChildren()) {
                sortChildren(child);
            }
        }
    }

    /**
     * ПОДСЧЁТ ВСЕХ УЗЛОВ В ИЕРАРХИИ
     */
    private int countAllNodes(List<AssemblyHierarchyDto> nodes) {
        int count = 0;
        for (AssemblyHierarchyDto node : nodes) {
            count++;
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                count += countAllNodes(node.getChildren());
            }
        }
        return count;
    }

    // ========================================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ========================================================================

    private String getStatusText(Short flagStatus) {
        if (flagStatus == null) {
            return "⚪ Неизвестно";
        }
        return switch (flagStatus) {
            case 0 -> "✅ Активна";
            case 1 -> "🔄 В работе";
            case 2 -> "❌ Закрыта";
            case 3 -> "📋 На согласовании";
            case 4 -> "✅ Согласована";
            case 5 -> "❌ Отклонена";
            default -> "⚪ Статус " + flagStatus;
        };
    }
}