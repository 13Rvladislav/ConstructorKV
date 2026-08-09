package com.example.constructorKV.common.service;

import com.example.constructorKV.common.annotation.ExternalEntity;
import com.example.constructorKV.common.annotation.ExternalField;
import com.example.constructorKV.common.dto.external.ExternalEntityDto;
import com.example.constructorKV.common.dto.external.ExternalFieldDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ExternalEntityService {

    public ExternalEntityDto buildEntityMetadata(Class<?> clazz) {
        ExternalEntity entityAnnotation = clazz.getAnnotation(ExternalEntity.class);
        if (entityAnnotation == null) {
            throw new IllegalArgumentException("Класс " + clazz.getName() + " не помечен аннотацией @ExternalEntity");
        }

        List<ExternalFieldDto> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            ExternalField fieldAnnotation = field.getAnnotation(ExternalField.class);
            if (fieldAnnotation == null) {
                fields.add(buildDefaultField(field));
            } else {
                fields.add(buildFieldFromAnnotation(field, fieldAnnotation));
            }
        }

        fields.sort((a, b) -> {
            int orderA = a.getDisplayOrder() != null ? a.getDisplayOrder() : Integer.MAX_VALUE;
            int orderB = b.getDisplayOrder() != null ? b.getDisplayOrder() : Integer.MAX_VALUE;
            return Integer.compare(orderA, orderB);
        });

        String entityName = entityAnnotation.name();
        if (entityName == null || entityName.isEmpty()) {
            entityName = clazz.getSimpleName();
        }

        return ExternalEntityDto.builder()
                .entityName(entityName)
                .displayName(entityAnnotation.displayName())
                .description(entityAnnotation.description())
                .fields(fields)
                .build();
    }

    private ExternalFieldDto buildFieldFromAnnotation(Field field, ExternalField annotation) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        String type = annotation.type().isEmpty() ? getTypeName(fieldType) : annotation.type();
        boolean isNavigation = annotation.isNavigation();
        String relatedEntity = isNavigation && annotation.relatedEntity().isEmpty()
                ? fieldType.getSimpleName()
                : annotation.relatedEntity();

        return ExternalFieldDto.builder()
                .name(fieldName)
                .displayName(annotation.displayName().isEmpty() ? formatDisplayName(fieldName) : annotation.displayName())
                .type(type)
                .isRequired(annotation.isRequired())
                .isKey(annotation.isKey())
                .isNavigation(isNavigation)
                .relatedEntity(relatedEntity)
                .category(annotation.category())
                .displayOrder(annotation.displayOrder())
                .build();
    }

    private ExternalFieldDto buildDefaultField(Field field) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        boolean isNavigation = isNavigationType(fieldType);
        String relatedEntity = isNavigation ? fieldType.getSimpleName() : null;

        return ExternalFieldDto.builder()
                .name(fieldName)
                .displayName(formatDisplayName(fieldName))
                .type(getTypeName(fieldType))
                .isRequired(true)
                .isKey(fieldName.equalsIgnoreCase("id"))
                .isNavigation(isNavigation)
                .relatedEntity(relatedEntity)
                .category("Основная информация")
                .build();
    }

    private String getTypeName(Class<?> type) {
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Long.class || type == long.class) return "integer";
        if (type == Double.class || type == double.class) return "decimal";
        if (type == Float.class || type == float.class) return "decimal";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        if (type == String.class) return "string";
        if (type == java.time.LocalDateTime.class || type == java.util.Date.class) return "datetime";
        if (type.isEnum()) return "enum";
        if (type == java.util.UUID.class) return "guid";
        return "object";
    }

    private boolean isNavigationType(Class<?> type) {
        String packageName = type.getPackage().getName();
        return !type.isPrimitive()
                && !type.isEnum()
                && !type.equals(String.class)
                && !type.equals(Integer.class)
                && !type.equals(Long.class)
                && !type.equals(Double.class)
                && !type.equals(Float.class)
                && !type.equals(Boolean.class)
                && !type.equals(java.time.LocalDateTime.class)
                && !type.equals(java.util.Date.class)
                && !type.equals(java.util.UUID.class)
                && !packageName.startsWith("java.")
                && !packageName.startsWith("javax.")
                && !packageName.startsWith("jakarta.")
                && !packageName.startsWith("lombok.");
    }

    private String formatDisplayName(String fieldName) {
        String result = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }
}