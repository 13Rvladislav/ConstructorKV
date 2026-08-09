package com.example.constructorKV.specif.service;

import com.example.constructorKV.common.exception.BusinessException;
import com.example.constructorKV.specif.dto.SpecifNodeDto;
import com.example.constructorKV.specif.dto.SpecifTreeResponseDto;
import com.example.constructorKV.specif.repository.SpecifRepository;
import com.example.constructorKV.tree.entity.TemaEntity;
import com.example.constructorKV.tree.repository.TemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecifService {

    private final SpecifRepository specifRepository;
    private final TemaRepository temaRepository;

    public SpecifTreeResponseDto buildSpecifTree(Integer temaId, Integer izdelieId, Integer groupeId) {
        log.info("🔨 Построение дерева спецификаций для темы {}, изделия {}, группы {}",
                temaId, izdelieId, groupeId);

        try {
            Integer rootId = specifRepository.findRootSpecifId(temaId, izdelieId, groupeId);

            if (rootId == null) {
                return SpecifTreeResponseDto.builder()
                        .groupInfo(buildGroupInfo(temaId, izdelieId, groupeId))
                        .roots(new ArrayList<>())
                        .totalNodes(0)
                        .version("1.0")
                        .build();
            }

            log.info("📍 Найдена корневая спецификация: ID = {}", rootId);

            List<Object[]> treeData = specifRepository.buildSpecifTree(rootId);

            if (treeData == null || treeData.isEmpty()) {
                return SpecifTreeResponseDto.builder()
                        .groupInfo(buildGroupInfo(temaId, izdelieId, groupeId))
                        .roots(new ArrayList<>())
                        .totalNodes(0)
                        .version("1.0")
                        .build();
            }

            log.info("📊 Получено {} строк из БД", treeData.size());

            List<SpecifNodeDto> roots = buildTreeFromData(treeData);
            int totalNodes = countNodes(roots);

            log.info("✅ Дерево построено. Корней: {}, Всего узлов: {}", roots.size(), totalNodes);

            return SpecifTreeResponseDto.builder()
                    .groupInfo(buildGroupInfo(temaId, izdelieId, groupeId))
                    .roots(roots)
                    .totalNodes(totalNodes)
                    .version("1.0")
                    .build();

        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage(), e);
            throw new BusinessException("Не удалось построить дерево: " + e.getMessage(), e);
        }
    }

    /**
     * ПОСТРОЕНИЕ ДЕРЕВА ИЗ ПЛОСКОГО СПИСКА
     */
    private List<SpecifNodeDto> buildTreeFromData(List<Object[]> treeData) {
        if (treeData == null || treeData.isEmpty()) {
            return new ArrayList<>();
        }

        // Ключ: уникальный ID (оригинальный ID из SPECIF22)
        Map<Integer, SpecifNodeDto> nodeMap = new LinkedHashMap<>();
        List<SpecifNodeDto> roots = new ArrayList<>();

        // ШАГ 1: Создаём все узлы по оригинальному ID
        for (Object[] row : treeData) {
            Integer id = (Integer) row[0];          // оригинальный ID
            Integer parentId = (Integer) row[1];
            Integer linkImpl = (Integer) row[2];
            String obozn = (String) row[3];
            String naim = (String) row[4];
            Double massa = (Double) row[5];
            String letters = (String) row[6];
            Short flag = (Short) row[7];
            Integer level = (Integer) row[8];
            Integer displayId = (Integer) row[9];
            String linkStatus = (String) row[10];

            boolean isLinked = linkImpl != null && linkImpl > 0;

            // Используем display_id как ключ, но храним оригинальный ID
            SpecifNodeDto node = SpecifNodeDto.builder()
                    .id(id)  // оригинальный ID
                    .displayId(displayId)  // ID для отображения (с учетом LINK)
                    .parentId(parentId)
                    .obozn(obozn != null ? obozn : "")
                    .naim(naim != null ? naim : "")
                    .massa(massa)
                    .letters(letters)
                    .flag(flag)
                    .level(level)
                    .isLinked(isLinked)
                    .linkInfo(isLinked ? "🔗 Заимствовано из ID " + linkImpl : null)
                    .children(new ArrayList<>())
                    .build();

            // Используем displayId как ключ в мапе
            nodeMap.put(displayId, node);
        }

        log.info("📦 Создано {} узлов", nodeMap.size());

        // ШАГ 2: Строим связи по display_id
        for (Map.Entry<Integer, SpecifNodeDto> entry : nodeMap.entrySet()) {
            SpecifNodeDto node = entry.getValue();

            if (node.getParentId() == null) {
                roots.add(node);
                continue;
            }

            // Ищем родителя по display_id
            SpecifNodeDto parent = nodeMap.get(node.getParentId());
            if (parent != null) {
                parent.getChildren().add(node);
            } else {
                // Родитель не найден — это корень
                roots.add(node);
            }
        }

        // ШАГ 3: Сортируем
        for (SpecifNodeDto root : roots) {
            sortChildren(root);
        }

        return roots;
    }

    private void sortChildren(SpecifNodeDto node) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            node.getChildren().sort(Comparator.comparing(
                    SpecifNodeDto::getObozn,
                    String.CASE_INSENSITIVE_ORDER
            ));
            for (SpecifNodeDto child : node.getChildren()) {
                sortChildren(child);
            }
        }
    }

    private int countNodes(List<SpecifNodeDto> roots) {
        int count = 0;
        for (SpecifNodeDto root : roots) {
            count += countNodesRecursive(root);
        }
        return count;
    }

    private int countNodesRecursive(SpecifNodeDto node) {
        if (node == null) return 0;
        int count = 1;
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (SpecifNodeDto child : node.getChildren()) {
                count += countNodesRecursive(child);
            }
        }
        return count;
    }

    private SpecifTreeResponseDto.GroupInfo buildGroupInfo(Integer temaId, Integer izdelieId, Integer groupeId) {
        String temaName = temaRepository.findById(temaId.longValue())
                .map(TemaEntity::getNaim)
                .orElse("Тема " + temaId);

        String izdelieName = "Изделие " + izdelieId;
        try {
            List<Object[]> izdInfo = temaRepository.findIzdelieById(izdelieId);
            if (!izdInfo.isEmpty()) {
                izdelieName = (String) izdInfo.get(0)[0];
                if (izdelieName == null || izdelieName.isEmpty()) {
                    izdelieName = "Изделие " + izdelieId;
                }
            }
        } catch (Exception e) {
            log.warn("Не удалось получить название изделия: {}", e.getMessage());
        }

        return SpecifTreeResponseDto.GroupInfo.builder()
                .temaId(temaId)
                .temaName(temaName)
                .izdelieId(izdelieId)
                .izdelieName(izdelieName)
                .groupeId(groupeId)
                .groupeName("Группа " + groupeId)
                .build();
    }
}