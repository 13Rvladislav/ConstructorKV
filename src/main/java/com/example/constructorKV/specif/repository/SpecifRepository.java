package com.example.constructorKV.specif.repository;

import com.example.constructorKV.specif.entity.Specif22Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ СО СПЕЦИФИКАЦИЯМИ
 *
 * Предоставляет методы для доступа к данным таблиц SPECIF22.
 *
 * Основные методы:
 * 1. findRootSpecifId() - поиск корневой спецификации по группе
 * 2. buildSpecifTree() - построение дерева от корня с учётом LINK_IMPLEMETATION
 *
 * @Repository - указывает, что это репозиторий
 */
@Repository
public interface SpecifRepository extends JpaRepository<Specif22Entity, Integer> {

    // ========================================================================
    // ШАГ 1: ПОИСК КОРНЕВОЙ СПЕЦИФИКАЦИИ
    // ========================================================================

    /**
     * НАХОДИТ ID КОРНЕВОЙ (ГОЛОВНОЙ) СПЕЦИФИКАЦИИ
     *
     * По теме, изделию и группе находит спецификацию,
     * у которой нет родителя (ID_PARENT IS NULL).
     *
     * @param temaId ID темы
     * @param izdelieId ID изделия
     * @param groupeId ID группы
     * @return ID корневой спецификации или null
     */
    @Query(value = """
        SELECT DISTINCT s."ID"
        FROM "Konstructor"."SPECIF22" s
        WHERE s."ID_TEMA" = :temaId
          AND s."ID_IZDELIE" = :izdelieId
          AND s."ID_GROUPE" = :groupeId
          AND s."ID_PARENT" IS NULL
        ORDER BY s."ID" DESC
        """, nativeQuery = true)
    Integer findRootSpecifId(
            @Param("temaId") Integer temaId,
            @Param("izdelieId") Integer izdelieId,
            @Param("groupeId") Integer groupeId
    );

    // ========================================================================
    // ШАГ 2: ПОСТРОЕНИЕ ДЕРЕВА ОТ КОРНЯ
    // ========================================================================

    /**
     * СТРОИТ ДЕРЕВО СПЕЦИФИКАЦИЙ ОТ КОРНЯ
     *
     * Использует рекурсивный CTE (WITH RECURSIVE) для обхода всей иерархии.
     *
     * Ключевая логика:
     * 1. Начинаем с корневой спецификации (ID = :rootId)
     * 2. Для каждого узла проверяем LINK_IMPLEMETATION
     * 3. Если есть LINK → подставляем данные из оригинальной спецификации
     * 4. Строим полный путь (sort_path) для правильной сортировки
     * 5. Добавляем статус "ССЫЛОЧНОСТЬ" для визуализации
     *
     * Возвращает List<Object[]>, где каждый массив содержит:
     * [0] - ID
     * [1] - ID_PARENT
     * [2] - LINK_IMPLEMETATION
     * [3] - OBOZN (показываемое)
     * [4] - NAIM (показываемое)
     * [5] - ССЫЛОЧНОСТЬ (статус)
     *
     * @param rootId ID корневой спецификации
     * @return список строк дерева
     */
    @Query(value = """
        WITH RECURSIVE "SpecHierarchy" AS (
            -- Базовый уровень: корневая спецификация
            SELECT 
                "ID",
                "ID_PARENT",
                "LINK_IMPLEMETATION",
                "OBOZN",
                "NAIM",
                0 AS level,
                CAST("OBOZN" AS VARCHAR(500)) AS sort_path,
                CASE 
                    WHEN "LINK_IMPLEMETATION" IS NOT NULL 
                    THEN 'ЗАИМСТВОВАНО (→ ID ' || "LINK_IMPLEMETATION" || ')'
                    ELSE 'ОРИГИНАЛ'
                END AS "ССЫЛОЧНОСТЬ"
            FROM "Konstructor"."SPECIF22"
            WHERE "ID" = :rootId

            UNION ALL

            -- Рекурсивный уровень: дети с учётом LINK_IMPLEMETATION
            SELECT 
                CASE 
                    WHEN s."LINK_IMPLEMETATION" IS NOT NULL THEN s2."ID" 
                    ELSE s."ID" 
                END AS "ID",
                s."ID_PARENT",
                CASE 
                    WHEN s."LINK_IMPLEMETATION" IS NOT NULL THEN s2."LINK_IMPLEMETATION" 
                    ELSE s."LINK_IMPLEMETATION" 
                END AS "LINK_IMPLEMETATION",
                CASE 
                    WHEN s."LINK_IMPLEMETATION" IS NOT NULL THEN s2."OBOZN" 
                    ELSE s."OBOZN" 
                END AS "OBOZN",
                CASE 
                    WHEN s."LINK_IMPLEMETATION" IS NOT NULL THEN s2."NAIM" 
                    ELSE s."NAIM" 
                END AS "NAIM",
                sh.level + 1,
                CAST(sh.sort_path || ' → ' || COALESCE(
                    CASE 
                        WHEN s."LINK_IMPLEMETATION" IS NOT NULL THEN s2."OBOZN" 
                        ELSE s."OBOZN" 
                    END,
                    'NULL'
                ) AS VARCHAR(500)) AS sort_path,
                CASE 
                    WHEN s."LINK_IMPLEMETATION" IS NOT NULL 
                    THEN 'ЗАИМСТВОВАНО (→ ID ' || s."LINK_IMPLEMETATION" || ')'
                    ELSE 'ОРИГИНАЛ'
                END AS "ССЫЛОЧНОСТЬ"
            FROM "Konstructor"."SPECIF22" s
            LEFT JOIN "Konstructor"."SPECIF22" s2 ON s."LINK_IMPLEMETATION" = s2."ID"
            INNER JOIN "SpecHierarchy" sh ON s."ID_PARENT" = sh."ID"
        )
        SELECT 
            "ID", 
            "ID_PARENT", 
            "LINK_IMPLEMETATION", 
            "OBOZN", 
            "NAIM",
            "ССЫЛОЧНОСТЬ"
        FROM "SpecHierarchy"
        ORDER BY sort_path
        """, nativeQuery = true)
    List<Object[]> buildSpecifTree(@Param("rootId") Integer rootId);
}