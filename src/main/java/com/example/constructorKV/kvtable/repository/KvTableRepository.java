package com.example.constructorKV.kvtable.repository;

import com.example.constructorKV.kv.entity.KomplektovkaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * РЕПОЗИТОРИЙ ДЛЯ ТАБЛИЦЫ КВ
 *
 * Содержит методы для получения данных,
 * необходимых для построения сводной таблицы КВ.
 *
 * @Repository - указывает, что это Spring Data репозиторий
 */
@Repository
public interface KvTableRepository extends JpaRepository<KomplektovkaEntity, Integer> {

    // ========================================================================
    // МЕТОДЫ ДЛЯ СПЕЦИФИКАЦИЙ
    // ========================================================================

    /**
     * ПОЛУЧИТЬ ВСЕ ID СПЕЦИФИКАЦИЙ В ДЕРЕВЕ С УЧЕТОМ LINK_IMPLEMETATION
     *
     * Использует рекурсивный CTE для обхода дерева спецификаций.
     * Возвращает оригинальные ID спецификаций (не ID из LINK_IMPLEMETATION).
     *
     * @param rootSpecifId ID корневой спецификации
     * @param temaId ID темы
     * @param izdelieId ID изделия
     * @param groupeId ID группы
     * @return список ID спецификаций
     */
    @Query(value = """
        WITH RECURSIVE spec_tree AS (
            -- Базовый уровень: корневая спецификация
            SELECT 
                s."ID" AS original_id,
                s."ID" AS current_id,
                s."ID_PARENT",
                s."LINK_IMPLEMETATION"
            FROM "Konstructor"."SPECIF22" s
            WHERE s."ID" = :rootSpecifId
              AND s."ID_TEMA" = :temaId
              AND s."ID_IZDELIE" = :izdelieId
              AND s."ID_GROUPE" = :groupeId
            
            UNION ALL
            
            -- Рекурсивный уровень: дети
            SELECT 
                s."ID" AS original_id,
                COALESCE(s."LINK_IMPLEMETATION", s."ID") AS current_id,
                s."ID_PARENT",
                s."LINK_IMPLEMETATION"
            FROM "Konstructor"."SPECIF22" s
            INNER JOIN spec_tree st ON s."ID_PARENT" = st.current_id
            WHERE s."ID_TEMA" = :temaId
              AND s."ID_IZDELIE" = :izdelieId
              AND s."ID_GROUPE" = :groupeId
        )
        SELECT DISTINCT st.original_id AS specif_id
        FROM spec_tree st
        """, nativeQuery = true)
    List<Integer> findAllSpecifIdsInTree(
            @Param("rootSpecifId") Integer rootSpecifId,
            @Param("temaId") Integer temaId,
            @Param("izdelieId") Integer izdelieId,
            @Param("groupeId") Integer groupeId
    );

    /**
     * ПРОВЕРИТЬ, ЯВЛЯЕТСЯ ЛИ СПЕЦИФИКАЦИЯ СБОРКОЙ
     *
     * @param specifId ID спецификации
     * @return true если есть структура с ID_DIVISION = 3
     */
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM "Konstructor"."STRUCTURE_SPECIF"
        WHERE "ID_SPECIF" = :specifId
          AND "ID_DIVISION" = 3
        """, nativeQuery = true)
    boolean isAssembly(@Param("specifId") Integer specifId);

    // ========================================================================
    // МЕТОДЫ ДЛЯ СЛУЖЕБНЫХ ЗАПИСОК
    // ========================================================================

    /**
     * НАЙТИ ВСЕ СЛУЖЕБНЫЕ ЗАПИСКИ ДЛЯ СПИСКА СПЕЦИФИКАЦИЙ
     *
     * ID_SPECIF в SBORKI_SL_ZAP может содержать несколько значений через запятую.
     *
     * Возвращает Object[] где:
     * [0] - SL_ZAPISKA.ID
     * [1] - SL_ZAPISKA.OBOZN
     * [2] - SL_ZAPISKA.NAIM_SL_ZAP
     * [3] - SL_ZAPISKA.FLAG_STATUS
     * [4] - SL_ZAPISKA.SOST_SOGL_ID
     * [5] - SBORKI_SL_ZAP.ID_SPECIF (строка с ID через запятую)
     * [6] - IZDEL_SL_ZAP.ID_IZD
     * [7] - IZDELIE.NAIM_IZD
     *
     * @param specifIds список ID спецификаций
     * @return список Object[] с данными
     */
    @Query(value = """
        SELECT DISTINCT
            sl."ID" AS sl_zap_id,
            sl."OBOZN" AS sl_zap_obozn,
            sl."NAIM_SL_ZAP" AS sl_zap_naim,
            sl."FLAG_STATUS" AS sl_zap_status,
            sl."SOST_SOGL_ID" AS sost_sogl_id,
            ssz."ID_SPECIF" AS specif_ids,
            iz."ID_IZD" AS izdelie_id,
            izd."NAIM_IZD" AS izdelie_name
        FROM "Konstructor"."SBORKI_SL_ZAP" ssz
        INNER JOIN "Konstructor"."IZDEL_SL_ZAP" iz ON ssz."ID_IZDEL_SL_ZAP" = iz."ID"
        INNER JOIN "Konstructor"."SL_ZAPISKA" sl ON iz."ID_SL_ZAP" = sl."ID"
        LEFT JOIN "Konstructor"."IZDELIE" izd ON iz."ID_IZD" = izd."ID"
        WHERE 
            ssz."ID_SPECIF" IN (:specifIds)
            OR EXISTS (
                SELECT 1 FROM unnest(string_to_array(ssz."ID_SPECIF", ',')) AS spec_id
                WHERE spec_id::integer IN (:specifIds)
            )
        ORDER BY sl."OBOZN"
        """, nativeQuery = true)
    List<Object[]> findSlZapBySpecifIds(@Param("specifIds") List<Integer> specifIds);

    // ========================================================================
    // МЕТОДЫ ДЛЯ КОМПЛЕКТОВОЧНЫХ ВЕДОМОСТЕЙ
    // ========================================================================

    /**
     * ПОЛУЧИТЬ ВСЕ КВ ДЛЯ СПИСКА СЛУЖЕБНЫХ ЗАПИСОК
     *
     * @param slZapIds список ID служебных записок
     * @return список КВ
     */
    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz IN :slZapIds ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findKvBySlZapIds(@Param("slZapIds") List<Integer> slZapIds);

    /**
     * ПОЛУЧИТЬ СТАТУС СОГЛАСОВАНИЯ И ДАТУ ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ
     *
     * Возвращает Object[] где:
     * [0] - is_agreed (boolean)
     * [1] - DATE_APPROVED (timestamp)
     *
     * @param slZapiskaId ID служебной записки
     * @return Object[] с данными
     */
    @Query(value = """
        SELECT 
            CASE 
                WHEN s."SOST_SOGL_ID" IS NOT NULL 
                 AND ss."DATE_APPROVED" IS NOT NULL 
                THEN true
                ELSE false
            END AS is_agreed,
            ss."DATE_APPROVED" AS agreed_date
        FROM "Konstructor"."SL_ZAPISKA" s
        LEFT JOIN "Konstructor"."STATE_SOGLAS" ss ON s."SOST_SOGL_ID" = ss."ID"
        WHERE s."ID" = :slZapiskaId
        """, nativeQuery = true)
    List<Object[]> findAgreedStatus(@Param("slZapiskaId") Integer slZapiskaId);



    /**
     * ПОЛУЧИТЬ ВСЕ КВ ДЛЯ СПИСКА СЛУЖЕБНЫХ ЗАПИСОК
     */
    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz IN :slZapIds ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findBySlZapIds(@Param("slZapIds") List<Integer> slZapIds);
}