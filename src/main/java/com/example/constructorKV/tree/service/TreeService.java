package com.example.constructorKV.tree.service;

import com.example.constructorKV.common.exception.BusinessException;
import com.example.constructorKV.tree.dto.FullTreeResponseDto;
import com.example.constructorKV.tree.dto.TemaInfoResponseDto;
import com.example.constructorKV.tree.entity.TemaEntity;
import com.example.constructorKV.tree.repository.TemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TreeService {

    private final TemaRepository temaRepository;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // ========================================================================
    // ПОЛНОЕ ДЕРЕВО
    // ========================================================================

    /**
     * ПОСТРОЕНИЕ ПОЛНОГО ДЕРЕВА
     *
     * Возвращает: Темы → Изделия → Группы (полная вложенная структура)
     * Используется для: GET /api/tree/full и GET /api/tree/tema/{temaId}
     */
    public FullTreeResponseDto buildFullTree() {
        log.info("🔨 Построение полного дерева: Темы → Изделия → Группы");

        try {
            List<TemaEntity> allTemas = temaRepository.findAllOrdered();

            if (allTemas.isEmpty()) {
                log.warn("⚠️ Нет данных для построения дерева");
                return FullTreeResponseDto.empty();
            }

            List<FullTreeResponseDto.TemaNode> temaNodes = new ArrayList<>();
            int totalNodes = 0;

            for (TemaEntity tema : allTemas) {
                // === УЗЕЛ ТЕМЫ ===
                FullTreeResponseDto.TemaNode.TemaNodeBuilder temaBuilder =
                        FullTreeResponseDto.TemaNode.builder()
                                .id(tema.getId().intValue())
                                .name(tema.getNaim() != null ? tema.getNaim() : "Без названия")
                                .indeksKd(tema.getIndeksKd());

                List<FullTreeResponseDto.IzdelieNode> izdelieNodes = new ArrayList<>();

                // Получаем все изделия для темы
                List<Object[]> izdeliaData = temaRepository.findIzdeliaByTemaId(
                        tema.getId().intValue());

                for (Object[] izdRow : izdeliaData) {
                    Integer izdelieId = (Integer) izdRow[0];
                    String izdelieNaim = (String) izdRow[1];
                    String izdelieFullName = (String) izdRow[2];
                    Object dateObj = izdRow[3];

                    // Конвертация даты
                    String dateStr = null;
                    if (dateObj != null) {
                        if (dateObj instanceof Instant) {
                            dateStr = ((Instant) dateObj)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .format(DATE_FORMATTER);
                        } else if (dateObj instanceof java.sql.Timestamp) {
                            dateStr = ((java.sql.Timestamp) dateObj)
                                    .toLocalDateTime()
                                    .format(DATE_FORMATTER);
                        } else if (dateObj instanceof java.time.LocalDateTime) {
                            dateStr = ((java.time.LocalDateTime) dateObj)
                                    .format(DATE_FORMATTER);
                        }
                    }

                    // === УЗЕЛ ИЗДЕЛИЯ ===
                    FullTreeResponseDto.IzdelieNode.IzdelieNodeBuilder izdelieBuilder =
                            FullTreeResponseDto.IzdelieNode.builder()
                                    .id(izdelieId)
                                    .name(izdelieNaim != null ? izdelieNaim : "Без названия")
                                    .fullName(izdelieFullName != null ? izdelieFullName : "")
                                    .dateAdd(dateStr);

                    List<FullTreeResponseDto.GroupeNode> groupeNodes = new ArrayList<>();

                    // Получаем группы для изделия
                    List<Object[]> groups = temaRepository.findGroupsByTemaAndIzdelie(
                            tema.getId().intValue(), izdelieId);

                    for (Object[] grRow : groups) {
                        Integer groupeId = (Integer) grRow[0];
                        String groupeNaim = (String) grRow[1];

                        FullTreeResponseDto.GroupeNode groupeNode =
                                FullTreeResponseDto.GroupeNode.builder()
                                        .id(groupeId)
                                        .name(groupeNaim != null ? groupeNaim : "Без названия")
                                        .build();

                        groupeNodes.add(groupeNode);
                        totalNodes++;
                    }

                    izdelieBuilder.groupes(groupeNodes);
                    izdelieNodes.add(izdelieBuilder.build());
                    totalNodes++;
                }

                temaBuilder.izdelia(izdelieNodes);
                temaNodes.add(temaBuilder.build());
                totalNodes++;
            }

            log.info("✅ Полное дерево построено. Всего узлов: {}", totalNodes);

            return FullTreeResponseDto.builder()
                    .temas(temaNodes)
                    .totalNodes(totalNodes)
                    .version("1.0")
                    .build();

        } catch (Exception e) {
            log.error("❌ Ошибка при построении дерева: {}", e.getMessage(), e);
            throw new BusinessException("Не удалось построить дерево: " + e.getMessage(), e);
        }
    }

    /**
     * ПОСТРОЕНИЕ ДЕРЕВА ДЛЯ КОНКРЕТНОЙ ТЕМЫ
     *
     * Возвращает полное дерево для одной темы.
     */
    public FullTreeResponseDto buildTreeByTemaId(Integer temaId) {
        log.info("🔨 Построение дерева для темы ID: {}", temaId);

        try {
            if (temaId == null) {
                log.warn("⚠️ temaId = null");
                return FullTreeResponseDto.empty();
            }

            Optional<TemaEntity> temaOpt = temaRepository.findById(temaId.longValue());
            if (temaOpt.isEmpty()) {
                log.warn("⚠️ Тема с ID {} не найдена", temaId);
                return FullTreeResponseDto.empty();
            }

            TemaEntity tema = temaOpt.get();

            // === УЗЕЛ ТЕМЫ ===
            FullTreeResponseDto.TemaNode.TemaNodeBuilder temaBuilder =
                    FullTreeResponseDto.TemaNode.builder()
                            .id(tema.getId().intValue())
                            .name(tema.getNaim() != null ? tema.getNaim() : "Без названия")
                            .indeksKd(tema.getIndeksKd());

            List<FullTreeResponseDto.IzdelieNode> izdelieNodes = new ArrayList<>();

            // Получаем все изделия для темы
            List<Object[]> izdeliaData = temaRepository.findIzdeliaByTemaId(
                    tema.getId().intValue());

            for (Object[] izdRow : izdeliaData) {
                Integer izdelieId = (Integer) izdRow[0];
                String izdelieNaim = (String) izdRow[1];
                String izdelieFullName = (String) izdRow[2];
                Object dateObj = izdRow[3];

                String dateStr = null;
                if (dateObj != null) {
                    if (dateObj instanceof Instant) {
                        dateStr = ((Instant) dateObj)
                                .atZone(java.time.ZoneId.systemDefault())
                                .format(DATE_FORMATTER);
                    } else if (dateObj instanceof java.sql.Timestamp) {
                        dateStr = ((java.sql.Timestamp) dateObj)
                                .toLocalDateTime()
                                .format(DATE_FORMATTER);
                    } else if (dateObj instanceof java.time.LocalDateTime) {
                        dateStr = ((java.time.LocalDateTime) dateObj)
                                .format(DATE_FORMATTER);
                    }
                }

                FullTreeResponseDto.IzdelieNode.IzdelieNodeBuilder izdelieBuilder =
                        FullTreeResponseDto.IzdelieNode.builder()
                                .id(izdelieId)
                                .name(izdelieNaim != null ? izdelieNaim : "Без названия")
                                .fullName(izdelieFullName != null ? izdelieFullName : "")
                                .dateAdd(dateStr);

                List<FullTreeResponseDto.GroupeNode> groupeNodes = new ArrayList<>();

                List<Object[]> groups = temaRepository.findGroupsByTemaAndIzdelie(
                        tema.getId().intValue(), izdelieId);

                for (Object[] grRow : groups) {
                    Integer groupeId = (Integer) grRow[0];
                    String groupeNaim = (String) grRow[1];

                    FullTreeResponseDto.GroupeNode groupeNode =
                            FullTreeResponseDto.GroupeNode.builder()
                                    .id(groupeId)
                                    .name(groupeNaim != null ? groupeNaim : "Без названия")
                                    .build();

                    groupeNodes.add(groupeNode);
                }

                izdelieBuilder.groupes(groupeNodes);
                izdelieNodes.add(izdelieBuilder.build());
            }

            temaBuilder.izdelia(izdelieNodes);

            // Подсчёт узлов
            int totalNodes = 1;
            for (FullTreeResponseDto.IzdelieNode izd : izdelieNodes) {
                totalNodes++;
                totalNodes += izd.getGroupes().size();
            }

            log.info("✅ Дерево для темы {} построено. Всего узлов: {}",
                    tema.getNaim(), totalNodes);

            return FullTreeResponseDto.builder()
                    .temas(List.of(temaBuilder.build()))
                    .totalNodes(totalNodes)
                    .version("1.0")
                    .build();

        } catch (Exception e) {
            log.error("❌ Ошибка при построении дерева для темы {}: {}",
                    temaId, e.getMessage(), e);
            throw new BusinessException("Не удалось построить дерево для темы: "
                    + e.getMessage(), e);
        }
    }

    // ========================================================================
    // ИНФОРМАЦИЯ О ТЕМЕ (БЕЗ ИЗДЕЛИЙ)
    // ========================================================================

    /**
     * ПОЛУЧЕНИЕ ИНФОРМАЦИИ О ТЕМЕ
     *
     * Возвращает ТОЛЬКО информацию о теме, БЕЗ вложенных изделий.
     * Используется для: GET /api/tree/tema/{temaId}/info
     *
     * @param temaId ID темы
     * @return TemaInfoResponseDto с информацией о теме
     */
    public TemaInfoResponseDto getTemaInfo(Long temaId) {
        log.info("🔍 Получение информации о теме {}", temaId);

        return temaRepository.findById(temaId)
                .map(tema -> {
                    // Создаём узел темы БЕЗ изделий
                    TemaInfoResponseDto.TemaInfoNode temaNode =
                            TemaInfoResponseDto.TemaInfoNode.builder()
                                    .id(tema.getId().intValue())
                                    .name(tema.getNaim())
                                    .indeksKd(tema.getIndeksKd())
                                    .build();

                    return TemaInfoResponseDto.builder()
                            .temas(List.of(temaNode))
                            .totalNodes(1)
                            .version("1.0")
                            .build();
                })
                .orElseThrow(() -> new BusinessException("Тема с ID " + temaId + " не найдена"));
    }

    /**
     * ПОИСК ТЕМ ПО НАЗВАНИЮ
     *
     * Возвращает ТОЛЬКО информацию о найденных темах, БЕЗ изделий.
     *
     * @param query поисковый запрос
     * @return TemaInfoResponseDto со списком найденных тем
     */
    public TemaInfoResponseDto searchTemas(String query) {
        log.info("🔍 Поиск тем по запросу: {}", query);

        if (query == null || query.trim().isEmpty()) {
            log.warn("⚠️ Поисковый запрос пуст");
            return TemaInfoResponseDto.empty();
        }

        List<TemaEntity> temas = temaRepository.findByNaimContainingIgnoreCase(query.trim());

        if (temas.isEmpty()) {
            log.info("По запросу '{}' ничего не найдено", query);
            return TemaInfoResponseDto.empty();
        }

        // Преобразуем в DTO БЕЗ изделий
        List<TemaInfoResponseDto.TemaInfoNode> temaNodes = temas.stream()
                .map(t -> TemaInfoResponseDto.TemaInfoNode.builder()
                        .id(t.getId().intValue())
                        .name(t.getNaim())
                        .indeksKd(t.getIndeksKd())
                        .build())
                .toList();

        log.info("Найдено {} тем по запросу '{}'", temaNodes.size(), query);

        return TemaInfoResponseDto.builder()
                .temas(temaNodes)
                .totalNodes(temaNodes.size())
                .version("1.0")
                .build();
    }
}