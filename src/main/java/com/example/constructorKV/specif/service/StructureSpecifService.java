package com.example.constructorKV.specif.service;

import com.example.constructorKV.common.exception.BusinessException;
import com.example.constructorKV.specif.dto.StructureSpecifDto;
import com.example.constructorKV.specif.entity.StructureSpecifEntity;
import com.example.constructorKV.specif.repository.StructureSpecifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * СЕРВИС РАБОТЫ СО СТРУКТУРОЙ СПЕЦИФИКАЦИИ
 *
 * @Service - указывает, что это Spring-сервис
 * @Slf4j - логирование
 * @RequiredArgsConstructor - конструктор для final полей
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StructureSpecifService {

    private final StructureSpecifRepository structureSpecifRepository;

    /**
     * ПОЛУЧИТЬ СТРУКТУРУ СПЕЦИФИКАЦИИ ПО ID
     *
     * @param specifId ID спецификации
     * @return список элементов структуры
     */
    public List<StructureSpecifDto> getStructureBySpecifId(Integer specifId) {
        log.info("🔍 Получение структуры для спецификации {}", specifId);

        if (specifId == null) {
            throw new BusinessException("ID спецификации не указан");
        }

        List<StructureSpecifEntity> entities = structureSpecifRepository.findBySpecifId(specifId);

        if (entities == null || entities.isEmpty()) {
            log.info("⚠️ Для спецификации {} нет структуры", specifId);
            return new ArrayList<>();
        }

        List<StructureSpecifDto> result = new ArrayList<>();
        for (StructureSpecifEntity entity : entities) {
            result.add(convertToDto(entity));
        }

        log.info("✅ Найдено {} элементов структуры для спецификации {}", result.size(), specifId);
        return result;
    }

    /**
     * КОНВЕРТАЦИЯ ENTITY → DTO
     */
    private StructureSpecifDto convertToDto(StructureSpecifEntity entity) {
        return StructureSpecifDto.builder()
                .id(entity.getId())
                .idSpecif(entity.getIdSpecif())
                .obozn(entity.getObozn())
                .naim(entity.getNaim())
                .quantity(entity.getQuantity())
                .massa(entity.getMassa())
                .material(entity.getMaterial())
                .idDivision(entity.getIdDivision() != null ? entity.getIdDivision().intValue() : null)
                .pos(entity.getPos())
                .format(entity.getFormat())
                .zone(entity.getZone())
                .build();
    }
}