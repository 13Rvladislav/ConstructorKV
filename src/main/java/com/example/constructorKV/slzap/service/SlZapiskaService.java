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
    // СУЩЕСТВУЮЩИЕ МЕТОДЫ
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

    public boolean isSlZapiskaAgreed(Integer slZapiskaId) {
        if (slZapiskaId == null) {
            return false;
        }
        return slZapiskaRepository.isSlZapiskaAgreed(slZapiskaId);
    }

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

        Integer productNumber = null;

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
            Integer idDivision = (Integer) row[11];

            if (izdelieNumber != null) {
                productNumber = izdelieNumber;
            }

            AssemblyHierarchyDto node = AssemblyHierarchyDto.builder()
                    .id(id)
                    .idSpecif(id)
                    .parentId(parentId)
                    .obozn(obozn != null ? obozn : "Без обозн.")
                    .naim(naim != null ? naim : "Без названия")
                    .quantity(quantity != null ? quantity : 1)
                    .massa(massa != null ? massa : 0.0)
                    .level(level != null ? level : 0)
                    .structureId(structureId)
                    .idDivision(idDivision)
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

        // Загружаем серии для всех узлов (сборок)
        loadSeriesForHierarchy(roots);

        // Сортируем детей
        for (AssemblyHierarchyDto root : roots) {
            sortChildren(root);
        }

        // Маркировка по сериям для сборок
        markBySeries(roots);

        // ===== ЗАГРУЖАЕМ СОДЕРЖИМОЕ С ПРОВЕРКОЙ СЕРИЙ ДЛЯ КАЖДОГО ЭЛЕМЕНТА =====
        loadContentForAssemblies(roots, productNumber);

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

    // ========================================================================
    // ЗАГРУЗКА СОДЕРЖИМОГО С ПРОВЕРКОЙ СЕРИЙ ДЛЯ КАЖДОГО ЭЛЕМЕНТА
    // ========================================================================

    private void loadContentForAssemblies(List<AssemblyHierarchyDto> hierarchy, Integer productNumber) {
        if (hierarchy == null || hierarchy.isEmpty()) {
            return;
        }

        for (AssemblyHierarchyDto node : hierarchy) {
            // Только для сборок, прошедших проверку по сериям
            if (node.getIdDivision() != null && node.getIdDivision() == 3
                    && node.getFitsSeries() != null && node.getFitsSeries()) {

                List<Object[]> content = slZapiskaRepository.findStructureContent(node.getId());
                if (content != null && !content.isEmpty()) {
                    log.debug("📦 Загрузка содержимого для сборки {} ({} элементов)",
                            node.getObozn(), content.size());

                    for (Object[] row : content) {
                        Integer id = (Integer) row[0];
                        Integer idSpecif = (Integer) row[1];
                        Integer idDivision = ((Short) row[2]).intValue();
                        String obozn = (String) row[3];
                        String naim = (String) row[4];
                        Integer quantityFromStruct = (Integer) row[5];
                        Double massaFromStruct = (Double) row[6];
                        String material = (String) row[7];
                        String pos = (String) row[8];
                        String format = (String) row[9];
                        String zone = (String) row[10];
                        Integer structureId = id;

                        // ============================================================
                        // ШАГ 1: ЗАГРУЖАЕМ СЕРИИ
                        // ============================================================
                        List<Object[]> seriesData = null;
                        try {
                            seriesData = slZapiskaRepository.findSeriesByStructureId(structureId);
                        } catch (Exception e) {
                            log.warn("⚠️ Не удалось загрузить серии для структуры {}: {}", structureId, e.getMessage());
                        }

                        // ============================================================
                        // ШАГ 2: ОПРЕДЕЛЯЕМ ЗНАЧЕНИЯ (по умолчанию из структуры)
                        // ============================================================
                        Integer finalQuantity = quantityFromStruct != null ? quantityFromStruct : 1;
                        Double finalMassa = massaFromStruct != null ? massaFromStruct : 0.0;
                        String fitReason = "Нет серий (действует на все)";
                        boolean fits = true;

                        // ============================================================
                        // ШАГ 3: ЕСЛИ ЕСТЬ СЕРИИ — ПРОВЕРЯЕМ
                        // ============================================================
                        if (seriesData != null && !seriesData.isEmpty()) {
                            boolean foundMatch = false;

                            for (Object[] sRow : seriesData) {
                                Integer mini = (Integer) sRow[0];
                                Integer maxi = (Integer) sRow[1];
                                Integer quantityFromSeries = (Integer) sRow[2];
                                Double massaFromSeries = (Double) sRow[3];
                                String dimensions = (String) sRow[4];

                                if (productNumber != null && mini != null && maxi != null) {
                                    if (productNumber >= mini && productNumber <= maxi) {
                                        // ПОДХОДИТ — берем из серийности
                                        if (quantityFromSeries != null) finalQuantity = quantityFromSeries;
                                        if (massaFromSeries != null) finalMassa = massaFromSeries;
                                        fitReason = "Подходит по сериям " + mini + "–" + maxi;
                                        foundMatch = true;
                                        fits = true;
                                        break;
                                    }
                                }
                            }

                            if (!foundMatch) {
                                fitReason = "Не подходит по сериям";
                                fits = false;
                            }
                        }

                        // ============================================================
                        // ШАГ 4: СОЗДАЕМ УЗЕЛ (ВСЕГДА, ДАЖЕ ЕСЛИ НЕ ПОДХОДИТ)
                        // ============================================================
                        AssemblyHierarchyDto contentNode = AssemblyHierarchyDto.builder()
                                .id(id)
                                .idSpecif(idSpecif)
                                .parentId(node.getId())
                                .obozn(obozn != null ? obozn : "Без обозн.")
                                .naim(naim != null ? naim : "Без названия")
                                .quantity(finalQuantity)
                                .massa(finalMassa)
                                .idDivision(idDivision)
                                .material(material != null ? material : "")
                                .note(pos != null ? "Поз." + pos : "")
                                .level(node.getLevel() != null ? node.getLevel() + 1 : 1)
                                .idRootSpecif(node.getIdRootSpecif())
                                .idOriginalStructure(id)
                                .structureId(structureId)
                                .izdelieNumber(productNumber)
                                .fitsSeries(fits)           // ← true/false для отображения
                                .fitReason(fitReason)       // ← причина для тултипа
                                .series(new ArrayList<>())
                                .children(new ArrayList<>())
                                .build();

                        // ============================================================
                        // ШАГ 5: ДОБАВЛЯЕМ СЕРИИ В УЗЕЛ (для отображения)
                        // ============================================================
                        if (seriesData != null && !seriesData.isEmpty()) {
                            for (Object[] sRow : seriesData) {
                                Integer mini = (Integer) sRow[0];
                                Integer maxi = (Integer) sRow[1];
                                Integer sQuantity = (Integer) sRow[2];
                                Double sMassa = (Double) sRow[3];
                                String dimensions = (String) sRow[4];

                                AssemblyHierarchyDto.SeriesInfo series = AssemblyHierarchyDto.SeriesInfo.builder()
                                        .mini(mini)
                                        .maxi(maxi)
                                        .quantity(sQuantity)
                                        .massa(sMassa)
                                        .dimensions(dimensions)
                                        .build();
                                contentNode.addSeries(series);
                            }
                        }

                        // ============================================================
                        // ШАГ 6: ДОБАВЛЯЕМ УЗЕЛ В РОДИТЕЛЯ (ВСЕГДА, ДЛЯ ОТОБРАЖЕНИЯ)
                        // ============================================================
                        node.addChild(contentNode);

                        log.debug("  {} {}: fits={}, reason={}, кол-во={}, масса={}",
                                contentNode.getTypeName(),
                                contentNode.getObozn(),
                                contentNode.getFitsSeries(),
                                contentNode.getFitReason(),
                                contentNode.getQuantity(),
                                contentNode.getMassa());
                    }
                }
            }

            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                loadContentForAssemblies(node.getChildren(), productNumber);
            }
        }
    }

    /**
     * ПРОВЕРКА СЕРИЙНОСТИ ДЛЯ ОДНОГО УЗЛА
     */
    private boolean checkNodeFitsSeries(AssemblyHierarchyDto node, Integer productNumber) {
        if (productNumber == null) {
            return true;
        }

        if (node.getSeries() == null || node.getSeries().isEmpty()) {
            return true;
        }

        for (AssemblyHierarchyDto.SeriesInfo series : node.getSeries()) {
            if (series.getMini() != null && series.getMaxi() != null) {
                if (productNumber >= series.getMini() && productNumber <= series.getMaxi()) {
                    return true;
                }
            }
        }

        return false;
    }

    // ========================================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ========================================================================

    private void loadSeriesForHierarchy(List<AssemblyHierarchyDto> hierarchy) {
        if (hierarchy == null || hierarchy.isEmpty()) {
            return;
        }

        for (AssemblyHierarchyDto node : hierarchy) {
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

            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                loadSeriesForHierarchy(node.getChildren());
            }
        }
    }

    private void markBySeries(List<AssemblyHierarchyDto> nodes) {
        for (AssemblyHierarchyDto node : nodes) {
            Integer productNumber = node.getIzdelieNumber();
            boolean nodeFits = node.checkFitsSeries(productNumber);

            if (!nodeFits) {
                markChildrenAsNonFitting(node);
            } else {
                if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                    markBySeries(node.getChildren());
                }
            }
        }
    }

    private void markChildrenAsNonFitting(AssemblyHierarchyDto node) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (AssemblyHierarchyDto child : node.getChildren()) {
                child.setFitsSeries(false);
                child.setFitReason("Родительский узел не прошёл по серийности");
                markChildrenAsNonFitting(child);
            }
        }
    }

    private AssemblyHierarchyDto createRootNode(Integer specifId) {
        return specifRepository.findById(specifId)
                .map(s -> AssemblyHierarchyDto.builder()
                        .id(s.getId())
                        .idSpecif(s.getId())
                        .obozn(s.getObozn() != null ? s.getObozn() : "Без обозн.")
                        .naim(s.getNaim() != null ? s.getNaim() : "Без названия")
                        .quantity(1)
                        .massa(s.getMassa() != null ? s.getMassa() : 0.0)
                        .parentId(null)
                        .level(0)
                        .structureId(null)
                        .idDivision(3)
                        .izdelieNumber(null)
                        .izdelieId(null)
                        .fitsSeries(true)
                        .fitReason("Корневая сборка")
                        .series(new ArrayList<>())
                        .children(new ArrayList<>())
                        .build())
                .orElseThrow(() -> new BusinessException("Спецификация с ID " + specifId + " не найдена"));
    }

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