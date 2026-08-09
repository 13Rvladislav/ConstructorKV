package com.example.constructorKV.kvtable.service;

import com.example.constructorKV.kv.entity.KomplektovkaEntity;
import com.example.constructorKV.kv.repository.KomplektovkaRepository;
import com.example.constructorKV.kv.service.KvStatusService;
import com.example.constructorKV.kvtable.dto.request.KvTableRequestDto;
import com.example.constructorKV.kvtable.dto.response.KvTableRowDto;
import com.example.constructorKV.kvtable.dto.response.KvTableResponseDto;
import com.example.constructorKV.slzap.repository.SlZapiskaRepository;
import com.example.constructorKV.specif.entity.Specif22Entity;
import com.example.constructorKV.specif.repository.SpecifRepository;
import com.example.constructorKV.tree.entity.TemaEntity;
import com.example.constructorKV.tree.repository.TemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KvTableService {

    private final SlZapiskaRepository slZapiskaRepository;
    private final KomplektovkaRepository komplektovkaRepository;
    private final SpecifRepository specifRepository;
    private final TemaRepository temaRepository;
    private final KvStatusService kvStatusService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public KvTableResponseDto getKvTable(KvTableRequestDto request) {
        Integer temaId = request.getTemaId();
        Integer izdelieId = request.getIzdelieId();
        Integer groupeId = request.getGroupeId();

        log.info("Построение таблицы КВ для темы {}, изделия {}, группы {}",
                temaId, izdelieId, groupeId);

        // ================================================================
        // ШАГ 1: Найти корневую спецификацию
        // ================================================================
        Integer rootSpecifId = specifRepository.findRootSpecifId(temaId, izdelieId, groupeId);
        if (rootSpecifId == null) {
            log.warn("Корневая спецификация не найдена");
            return buildEmptyResponse(temaId, izdelieId, groupeId);
        }

        // ================================================================
        // ШАГ 2: Найти ВСЕ спецификации в дереве
        // ================================================================
        List<Integer> allSpecifIds = slZapiskaRepository.findAllSpecifIdsInTreeNoLink(
                temaId, izdelieId, groupeId);

        if (allSpecifIds == null || allSpecifIds.isEmpty()) {
            log.warn("В дереве нет спецификаций");
            return buildEmptyResponse(temaId, izdelieId, groupeId);
        }

        // ================================================================
        // ШАГ 3: Ищем СЗ по ВСЕМ спецификациям из дерева
        // ================================================================
        String[] allSpecifIdArray = allSpecifIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        List<Object[]> slZapData = slZapiskaRepository.findSlZapBySpecifIds(allSpecifIdArray);

        if (slZapData == null || slZapData.isEmpty()) {
            log.warn("Для спецификаций не найдено СЗ");
            return buildEmptyResponse(temaId, izdelieId, groupeId);
        }

        // ================================================================
        // ШАГ 4: Группируем по СЗ и разворачиваем все ID_SPECIF
        // ================================================================
        Map<Integer, SlZapInfo> slZapMap = new LinkedHashMap<>();
        for (Object[] row : slZapData) {
            Integer slZapId = (Integer) row[0];
            String obozn = (String) row[1];
            String specifIdsStr = (String) row[5];

            SlZapInfo info = slZapMap.computeIfAbsent(slZapId, k -> new SlZapInfo());
            info.id = slZapId;
            info.obozn = obozn != null ? obozn : "Без обозн.";

            if (specifIdsStr != null && !specifIdsStr.isEmpty()) {
                String[] ids = specifIdsStr.split(",");
                for (String idStr : ids) {
                    try {
                        Integer parsedId = Integer.parseInt(idStr.trim());
                        if (!info.specifIds.contains(parsedId)) {
                            info.specifIds.add(parsedId);
                        }
                    } catch (NumberFormatException e) {
                        log.warn("Не удалось распарсить ID: {}", idStr);
                    }
                }
            }
        }

        // ================================================================
        // ШАГ 5: Получить даты согласования для всех СЗ
        // ================================================================
        for (Map.Entry<Integer, SlZapInfo> entry : slZapMap.entrySet()) {
            List<Object[]> agreedData = slZapiskaRepository.findAgreedStatus(entry.getKey());
            if (agreedData != null && !agreedData.isEmpty()) {
                Object[] row = agreedData.get(0);
                Boolean isAgreed = (Boolean) row[0];
                Object agreedDateObj = row[1];
                entry.getValue().isAgreed = isAgreed != null && isAgreed;
                if (agreedDateObj != null) {
                    if (agreedDateObj instanceof LocalDateTime) {
                        entry.getValue().agreedDate = ((LocalDateTime) agreedDateObj).format(DATE_FORMATTER);
                    } else if (agreedDateObj instanceof java.sql.Timestamp) {
                        entry.getValue().agreedDate = ((java.sql.Timestamp) agreedDateObj)
                                .toLocalDateTime().format(DATE_FORMATTER);
                    } else {
                        entry.getValue().agreedDate = agreedDateObj.toString();
                    }
                }
            }
        }

        // ================================================================
        // ⚠️ ФИЛЬТР: УБИРАЕМ НЕСОГЛАСОВАННЫЕ СЗ
        // ================================================================
        List<Integer> slZapIdsToRemove = new ArrayList<>();
        for (Map.Entry<Integer, SlZapInfo> entry : slZapMap.entrySet()) {
            if (entry.getValue().isAgreed == null || !entry.getValue().isAgreed) {
                slZapIdsToRemove.add(entry.getKey());
            }
        }
        for (Integer id : slZapIdsToRemove) {
            slZapMap.remove(id);
            log.info("🗑️ Исключена не согласованная СЗ-{}", id);
        }

        if (slZapMap.isEmpty()) {
            log.warn("Нет согласованных СЗ для отображения");
            return buildEmptyResponse(temaId, izdelieId, groupeId);
        }

        log.info("📊 Осталось {} согласованных СЗ", slZapMap.size());

        // ================================================================
        // ШАГ 6: Получить все КВ для этих СЗ
        // ================================================================
        List<Integer> slZapIds = new ArrayList<>(slZapMap.keySet());
        List<KomplektovkaEntity> allKv = komplektovkaRepository.findBySlZapIds(slZapIds);

        Map<Integer, List<KomplektovkaEntity>> kvBySlZap = allKv.stream()
                .collect(Collectors.groupingBy(KomplektovkaEntity::getIdSz));

        // ================================================================
        // ШАГ 7: Получить обозначения спецификаций
        // ================================================================
        Set<Integer> allSpecifIdsFromSlzap = new HashSet<>();
        for (SlZapInfo info : slZapMap.values()) {
            allSpecifIdsFromSlzap.addAll(info.specifIds);
        }
        Map<Integer, String> specifOboznMap = getSpecifOboznMap(new ArrayList<>(allSpecifIdsFromSlzap));

        // ================================================================
        // ШАГ 8: Формируем строки таблицы
        // ================================================================
        List<KvTableRowDto> rows = new ArrayList<>();
        int rowNumber = 0;

        for (Map.Entry<Integer, SlZapInfo> entry : slZapMap.entrySet()) {
            SlZapInfo info = entry.getValue();
            List<KomplektovkaEntity> kvList = kvBySlZap.getOrDefault(info.id, new ArrayList<>());

            Map<Integer, KomplektovkaEntity> kvBySpecif = new HashMap<>();
            for (KomplektovkaEntity kv : kvList) {
                try {
                    if (kv.getSborkiSz() != null && !kv.getSborkiSz().isEmpty()) {
                        Integer specifId = Integer.parseInt(kv.getSborkiSz());
                        kvBySpecif.put(specifId, kv);
                    }
                } catch (NumberFormatException e) {
                    log.warn("Не удалось распарсить SBORKI_SZ: {}", kv.getSborkiSz());
                }
            }

            for (Integer specifId : info.specifIds) {
                KomplektovkaEntity kv = kvBySpecif.get(specifId);
                rowNumber++;
                rows.add(createRow(rowNumber, info, kv, specifId, specifOboznMap));
            }
        }

        rows.sort(Comparator.comparing(KvTableRowDto::getSlZapiskaId)
                .thenComparing(KvTableRowDto::getNumber));

        int counter = 1;
        for (KvTableRowDto row : rows) {
            row.setNumber(counter++);
        }

        return KvTableResponseDto.builder()
                .groupInfo(buildGroupInfo(temaId, izdelieId, groupeId))
                .rows(rows)
                .totalRows(rows.size())
                .version("1.0")
                .build();
    }

    // ========================================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ========================================================================

    private KvTableRowDto createRow(
            int number,
            SlZapInfo info,
            KomplektovkaEntity kv,
            Integer specifId,
            Map<Integer, String> specifOboznMap) {

        String specifObozn = specifId != null ? specifOboznMap.getOrDefault(specifId, "") : "";

        String kvDesign = null;
        Integer kvId = null;
        String parentKvDesign = null;
        Integer kvStatusId = null;
        boolean hasKv = kv != null;

        if (kv != null) {
            kvId = kv.getId();
            kvDesign = kv.getDesign();
            kvStatusId = kv.getKvStatusId();
            if (kvDesign == null || kvDesign.isEmpty()) {
                kvDesign = "КВ-" + (specifObozn != null && !specifObozn.isEmpty() ? specifObozn : "СБ-" + specifId);
            }

            // Декомпозиция
            if (kv.getParentKvId() != null) {
                try {
                    KomplektovkaEntity parentKv = komplektovkaRepository.findById(kv.getParentKvId()).orElse(null);
                    if (parentKv != null) {
                        parentKvDesign = parentKv.getDesign();
                        if (parentKvDesign == null || parentKvDesign.isEmpty()) {
                            parentKvDesign = "КВ-" + parentKv.getId();
                        }
                    } else {
                        parentKvDesign = "—";
                    }
                } catch (Exception e) {
                    log.warn("Ошибка при получении родительской КВ: {}", e.getMessage());
                    parentKvDesign = "—";
                }
            }
        } else if (info.isAgreed != null && info.isAgreed) {
            // Фантомная КВ
            kvDesign = "КВ-" + (specifObozn != null && !specifObozn.isEmpty() ? specifObozn : "СБ-" + specifId);
            kvStatusId = null;
        }

        String status = getKvStatus(info, kv);

        return KvTableRowDto.builder()
                .number(number)
                .slZapiskaId(info.id)
                .slZapiskaObozn(info.obozn)
                .komplektovkaDesign(kvDesign)
                .komplektovkaId(kvId)
                .specifId(specifId)
                .slZapAgreedDate(info.agreedDate)
                .status(status)
                .parentKvDesign(parentKvDesign)
                .kvStatusId(kvStatusId)
                .isSlZapAgreed(info.isAgreed)  // <--- ВЕРНУТЬ
                .hasKv(hasKv)                  // <--- ВЕРНУТЬ
                .build();
    }

    private String getKvStatus(SlZapInfo info, KomplektovkaEntity kv) {
        // Если СЗ не согласована
        if (info.isAgreed == null || !info.isAgreed) {
            return "СЗ не согласована";
        }

        // Если КВ нет — "Не сформирована"
        if (kv == null) {
            return "Не сформирована";
        }

        // ===== СТАТУС ИЗ БД ПО ID =====
        // 1 - В работе, 4 - Запущена в производство, 5 - Аннулирована
        if (kv.getKvStatusId() != null) {
            String statusName = kvStatusService.getStatusNameById(kv.getKvStatusId());
            if (statusName != null) {
                return statusName;
            }
        }

        // ===== ОБРАТНАЯ СОВМЕСТИМОСТЬ =====
        if (kv.getDateArchiving() != null) {
            return "Аннулирована";
        }

        if (kv.getApprovedDate() != null) {
            return "Запущена в производство";
        }

        if (kv.getDateCreate() != null) {
            return "В работе";
        }

        return "Не сформирована";
    }

    private Map<Integer, String> getSpecifOboznMap(List<Integer> specifIds) {
        Map<Integer, String> map = new HashMap<>();
        if (specifIds == null || specifIds.isEmpty()) {
            return map;
        }
        for (Integer id : specifIds) {
            try {
                specifRepository.findById(id).ifPresent(s -> {
                    if (s.getObozn() != null) {
                        map.put(id, s.getObozn());
                    }
                });
            } catch (Exception ignored) {
            }
        }
        return map;
    }

    private KvTableResponseDto buildEmptyResponse(Integer temaId, Integer izdelieId, Integer groupeId) {
        return KvTableResponseDto.builder()
                .groupInfo(buildGroupInfo(temaId, izdelieId, groupeId))
                .rows(new ArrayList<>())
                .totalRows(0)
                .version("1.0")
                .build();
    }

    private KvTableResponseDto.GroupInfo buildGroupInfo(Integer temaId, Integer izdelieId, Integer groupeId) {
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

        return KvTableResponseDto.GroupInfo.builder()
                .temaId(temaId)
                .temaName(temaName)
                .izdelieId(izdelieId)
                .izdelieName(izdelieName)
                .groupeId(groupeId)
                .build();
    }

    // ========================================================================
    // ВНУТРЕННИЙ КЛАСС
    // ========================================================================

    private static class SlZapInfo {
        Integer id;
        String obozn;
        Boolean isAgreed = false;
        String agreedDate;
        List<Integer> specifIds = new ArrayList<>();
    }
}