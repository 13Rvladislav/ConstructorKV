package com.example.constructorKV.kv.repository;

import com.example.constructorKV.kv.entity.KomplektovkaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С КОМПЛЕКТОВОЧНЫМИ ВЕДОМОСТЯМИ
 *
 * Работает с таблицей KOMPLECTOVKA в схеме Konstructor.
 *
 * ВНИМАНИЕ: имя таблицы KOMPLECTOVKA (с одной К в середине)!
 *
 * @Repository - указывает, что это Spring Data репозиторий
 */
@Repository
public interface KomplektovkaRepository extends JpaRepository<KomplektovkaEntity, Integer> {

    /**
     * ПОЛУЧИТЬ ВСЕ КВ ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ
     *
     * @param idSz ID служебной записки
     * @return список КВ (все, включая архивные)
     */
    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz = :idSz ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findBySlZapiskaId(@Param("idSz") Integer idSz);

    /**
     * ПОЛУЧИТЬ АКТИВНЫЕ КВ ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ (не архивные)
     *
     * @param idSz ID служебной записки
     * @return список активных КВ
     */
    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz = :idSz AND k.dateArchiving IS NULL ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findActiveBySlZapiskaId(@Param("idSz") Integer idSz);

    /**
     * ПРОВЕРИТЬ, ЕСТЬ ЛИ ХОТЯ БЫ ОДНА КВ ДЛЯ СЛУЖЕБНОЙ ЗАПИСКИ
     *
     * @param idSz ID служебной записки
     * @return true если есть хотя бы одна КВ
     */
    @Query("SELECT COUNT(k) > 0 FROM KomplektovkaEntity k WHERE k.idSz = :idSz")
    boolean existsBySlZapiskaId(@Param("idSz") Integer idSz);

    /**
     * ПОЛУЧИТЬ СПИСОК ID СПЕЦИФИКАЦИЙ, НА КОТОРЫЕ УЖЕ ЕСТЬ КВ
     *
     * ВНИМАНИЕ: SBORKI_SZ хранит ОДИН ID сборки (не список через запятую)!
     */
    @Query(value = """
    SELECT DISTINCT 
        CAST(k."SBORKI_SZ" AS INTEGER)
    FROM "Konstructor"."KOMPLECTOVKA" k
    WHERE k."ID_SZ" = :slZapiskaId
      AND k."DATE_ARCHIVING" IS NULL
      AND k."SBORKI_SZ" IS NOT NULL
      AND k."SBORKI_SZ" != ''
    """, nativeQuery = true)
    List<Integer> findExistingSpecifIdsBySlZap(@Param("slZapiskaId") Integer slZapiskaId);
}