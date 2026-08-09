package com.example.constructorKV.kv.repository;

import com.example.constructorKV.kv.entity.KomplektovkaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KomplektovkaRepository extends JpaRepository<KomplektovkaEntity, Integer> {

    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz = :idSz ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findBySlZapiskaId(@Param("idSz") Integer idSz);

    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz = :idSz AND k.dateArchiving IS NULL ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findActiveBySlZapiskaId(@Param("idSz") Integer idSz);

    @Query("SELECT COUNT(k) > 0 FROM KomplektovkaEntity k WHERE k.idSz = :idSz")
    boolean existsBySlZapiskaId(@Param("idSz") Integer idSz);

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

    @Query("SELECT k FROM KomplektovkaEntity k WHERE k.idSz IN :slZapIds ORDER BY k.dateCreate DESC")
    List<KomplektovkaEntity> findBySlZapIds(@Param("slZapIds") List<Integer> slZapIds);
}