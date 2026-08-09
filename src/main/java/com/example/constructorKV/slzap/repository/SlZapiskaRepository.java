package com.example.constructorKV.slzap.repository;

import com.example.constructorKV.slzap.entity.SlZapiskaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlZapiskaRepository extends JpaRepository<SlZapiskaEntity, Integer> {

    @Query("SELECT s FROM SlZapiskaEntity s ORDER BY s.obozn")
    List<SlZapiskaEntity> findAllOrdered();

    @Query(value = """
        SELECT DISTINCT 
            sl."ID",
            sl."OBOZN",
            sl."NAIM_SL_ZAP",
            sl."FLAG_STATUS",
            izd."ID" AS "IZDELIE_ID",
            izd."NAIM_IZD" AS "IZDELIE_NAIM",
            iz."ID" AS "IZDEL_SL_ZAP_ID"
        FROM "Konstructor"."SBORKI_SL_ZAP" ssz
        INNER JOIN "Konstructor"."IZDEL_SL_ZAP" iz ON ssz."ID_IZDEL_SL_ZAP" = iz."ID"
        INNER JOIN "Konstructor"."SL_ZAPISKA" sl ON iz."ID_SL_ZAP" = sl."ID"
        LEFT JOIN "Konstructor"."IZDELIE" izd ON iz."ID_IZD" = izd."ID"
        WHERE 
            ssz."ID_SPECIF" = CAST(:specifId AS VARCHAR)
            OR POSITION(',' || CAST(:specifId AS VARCHAR) || ',' IN ',' || ssz."ID_SPECIF" || ',') > 0
        ORDER BY sl."OBOZN"
        """, nativeQuery = true)
    List<Object[]> findSlZapBySpecifId(@Param("specifId") Integer specifId);

    @Query(value = """
        SELECT 
            CASE 
                WHEN s."SOST_SOGL_ID" IS NOT NULL 
                 AND ss."DATE_DEVELOPER" IS NOT NULL 
                 AND ss."DATE_CHECKED_IT_OUT" IS NOT NULL 
                 AND ss."DATE_HEAD_DEPARTMENT" IS NOT NULL 
                 AND ss."DATE_NORM_CONTROL" IS NOT NULL 
                 AND ss."DATE_APPROVED" IS NOT NULL 
                THEN true
                ELSE false
            END AS is_agreed
        FROM "Konstructor"."SL_ZAPISKA" s
        LEFT JOIN "Konstructor"."STATE_SOGLAS" ss ON s."SOST_SOGL_ID" = ss."ID"
        WHERE s."ID" = :slZapiskaId
        """, nativeQuery = true)
    boolean isSlZapiskaAgreed(@Param("slZapiskaId") Integer slZapiskaId);

    @Query(value = """
        SELECT DISTINCT 
            s."ID" AS "SPECIF_ID",
            s."OBOZN" AS "SPECIF_OBOZN",
            izd."NAIM_IZD" AS "IZDELIE_NAIM"
        FROM "Konstructor"."IZDEL_SL_ZAP" iz
        INNER JOIN "Konstructor"."SBORKI_SL_ZAP" ssz ON ssz."ID_IZDEL_SL_ZAP" = iz."ID"
        CROSS JOIN LATERAL unnest(string_to_array(ssz."ID_SPECIF", ',')) AS spec_id
        INNER JOIN "Konstructor"."SPECIF22" s ON s."ID" = spec_id::integer
        LEFT JOIN "Konstructor"."IZDELIE" izd ON iz."ID_IZD" = izd."ID"
        WHERE iz."ID_SL_ZAP" = :slZapiskaId
        ORDER BY s."OBOZN"
        """, nativeQuery = true)
    List<Object[]> findAssembliesForSlZap(@Param("slZapiskaId") Integer slZapiskaId);

    // ========================================================================
    // МЕТОДЫ ДЛЯ KVTABLE (БЕЗ LINK)
    // ========================================================================

    @Query(value = """
        WITH RECURSIVE spec_tree AS (
            SELECT 
                s."ID" AS original_id,
                s."ID" AS current_id,
                s."ID_PARENT"
            FROM "Konstructor"."SPECIF22" s
            WHERE s."ID_TEMA" = :temaId
              AND s."ID_IZDELIE" = :izdelieId
              AND s."ID_GROUPE" = :groupeId
              AND s."ID_PARENT" IS NULL
            
            UNION ALL
            
            SELECT 
                s."ID" AS original_id,
                s."ID" AS current_id,
                s."ID_PARENT"
            FROM "Konstructor"."SPECIF22" s
            INNER JOIN spec_tree st ON s."ID_PARENT" = st.current_id
            WHERE s."ID_TEMA" = :temaId
              AND s."ID_IZDELIE" = :izdelieId
              AND s."ID_GROUPE" = :groupeId
        )
        SELECT DISTINCT st.original_id AS specif_id
        FROM spec_tree st
        """, nativeQuery = true)
    List<Integer> findAllSpecifIdsInTreeNoLink(
            @Param("temaId") Integer temaId,
            @Param("izdelieId") Integer izdelieId,
            @Param("groupeId") Integer groupeId
    );

    @Query(value = """
        SELECT DISTINCT
            sl."ID" AS sl_zap_id,
            sl."OBOZN" AS sl_zap_obozn,
            sl."NAIM_SL_ZAP" AS sl_zap_naim,
            sl."FLAG_STATUS" AS sl_zap_status,
            sl."SOST_SOGL_ID" AS sost_sogl_id,
            ssz."ID_SPECIF" AS specif_ids_raw,
            iz."ID_IZD" AS izdelie_id,
            izd."NAIM_IZD" AS izdelie_name,
            iz."ID" AS izdel_sl_zap_id
        FROM "Konstructor"."SBORKI_SL_ZAP" ssz
        INNER JOIN "Konstructor"."IZDEL_SL_ZAP" iz 
            ON ssz."ID_IZDEL_SL_ZAP" = iz."ID"
        INNER JOIN "Konstructor"."SL_ZAPISKA" sl 
            ON iz."ID_SL_ZAP" = sl."ID"
        LEFT JOIN "Konstructor"."IZDELIE" izd 
            ON iz."ID_IZD" = izd."ID"
        WHERE EXISTS (
            SELECT 1 
            FROM unnest(string_to_array(ssz."ID_SPECIF", ',')) AS spec_id
            WHERE spec_id = ANY(CAST(:specifIds AS text[]))
        )
        ORDER BY sl."OBOZN"
        """, nativeQuery = true)
    List<Object[]> findSlZapBySpecifIds(@Param("specifIds") String[] specifIds);

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

    // ========================================================================
    // МЕТОДЫ ДЛЯ ПРЕДПРОСМОТРА (С LINK)
    // ========================================================================

    @Query(value = """
        WITH RECURSIVE assembly_hierarchy AS (
            SELECT 
                s."ID" AS specif_id,
                CAST(NULL AS INTEGER) AS parent_id,
                s."OBOZN" AS obozn,
                s."NAIM" AS naim,
                1 AS quantity,
                COALESCE(s."MASSA", 0) AS massa,
                0 AS level,
                CAST(s."OBOZN" AS VARCHAR(1000)) AS full_path,
                CAST(s."ID" AS VARCHAR(1000)) AS path_ids,
                CAST(NULL AS INTEGER) AS structure_id,
                CAST(izd."NAIM_IZD" AS INTEGER) AS izdelie_number,
                izd."ID" AS izdelie_id,
                3 AS id_division
            FROM "Konstructor"."SPECIF22" s
            LEFT JOIN "Konstructor"."IZDELIE" izd ON s."ID_IZDELIE" = izd."ID"
            WHERE s."ID" = :specifId
            
            UNION ALL
            
            SELECT 
                CASE 
                    WHEN child."LINK_IMPLEMETATION" IS NOT NULL 
                    THEN linked."ID" 
                    ELSE child."ID" 
                END AS specif_id,
                parent.specif_id AS parent_id,
                CASE 
                    WHEN child."LINK_IMPLEMETATION" IS NOT NULL 
                    THEN linked."OBOZN" 
                    ELSE child."OBOZN" 
                END AS obozn,
                CASE 
                    WHEN child."LINK_IMPLEMETATION" IS NOT NULL 
                    THEN linked."NAIM" 
                    ELSE child."NAIM" 
                END AS naim,
                st."QUANTITY" AS quantity,
                COALESCE(st."RMASSA", 0) AS massa,
                parent.level + 1 AS level,
                CAST(parent.full_path || ' → ' || COALESCE(
                    CASE 
                        WHEN child."LINK_IMPLEMETATION" IS NOT NULL 
                        THEN linked."OBOZN" 
                        ELSE child."OBOZN" 
                    END,
                    'NULL'
                ) AS VARCHAR(1000)) AS full_path,
                CAST(parent.path_ids || ',' || 
                    CASE 
                        WHEN child."LINK_IMPLEMETATION" IS NOT NULL 
                        THEN linked."ID" 
                        ELSE child."ID" 
                    END 
                AS VARCHAR(1000)) AS path_ids,
                st."ID" AS structure_id,
                CAST(izd."NAIM_IZD" AS INTEGER) AS izdelie_number,
                izd."ID" AS izdelie_id,
                st."ID_DIVISION" AS id_division
            FROM assembly_hierarchy parent
            INNER JOIN "Konstructor"."STRUCTURE_SPECIF" st 
                ON st."ID_SPECIF" = parent.specif_id 
                AND st."ID_DIVISION" = 3
            INNER JOIN "Konstructor"."SPECIF22" child 
                ON child."OBOZN" = st."OBOZN"
            LEFT JOIN "Konstructor"."SPECIF22" linked 
                ON child."LINK_IMPLEMETATION" = linked."ID"
            LEFT JOIN "Konstructor"."IZDELIE" izd 
                ON child."ID_IZDELIE" = izd."ID"
            WHERE 
                st."OBOZN" IS NOT NULL 
                AND st."OBOZN" != ''
                AND child."ID" IS NOT NULL
                AND POSITION(',' || 
                    CASE 
                        WHEN child."LINK_IMPLEMETATION" IS NOT NULL 
                        THEN linked."ID" 
                        ELSE child."ID" 
                    END 
                || ',' IN ',' || parent.path_ids || ',') = 0
        )
        SELECT 
            specif_id,
            parent_id,
            obozn,
            naim,
            quantity,
            massa,
            level,
            full_path,
            structure_id,
            izdelie_number,
            izdelie_id,
            id_division
        FROM assembly_hierarchy
        ORDER BY full_path
        """, nativeQuery = true)
    List<Object[]> findAssemblyHierarchy(@Param("specifId") Integer specifId);

    @Query(value = """
        SELECT 
            st."ID",
            st."ID_SPECIF",
            st."ID_DIVISION",
            st."OBOZN",
            st."NAIM",
            st."QUANTITY",
            COALESCE(st."RMASSA", 0) AS RMASSA,
            st."MATERIAL",
            st."POS",
            st."FORMAT",
            st."ZONE"
        FROM "Konstructor"."STRUCTURE_SPECIF" st
        WHERE st."ID_SPECIF" = :specifId
          AND st."ID_DIVISION" IN (4, 5, 6, 7)
        ORDER BY st."ID_DIVISION", st."POS", st."OBOZN"
        """, nativeQuery = true)
    List<Object[]> findStructureContent(@Param("specifId") Integer specifId);

    @Query(value = """
        SELECT 
            "MINI",
            "MAXI",
            "QUANTITY",
            "MASSA",
            "DIMENSIONS"
        FROM "Konstructor"."PRODUCT_SERIES"
        WHERE "SSID" = :structureId
        ORDER BY "MINI"
        """, nativeQuery = true)
    List<Object[]> findSeriesByStructureId(@Param("structureId") Integer structureId);
}