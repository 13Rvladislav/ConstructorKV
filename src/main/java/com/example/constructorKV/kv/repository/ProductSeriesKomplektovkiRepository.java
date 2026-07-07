// src/main/java/com/example/constructorKV/kv/repository/ProductSeriesKomplektovkiRepository.java
package com.example.constructorKV.kv.repository;

import com.example.constructorKV.kv.entity.ProductSeriesKomplektovkiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSeriesKomplektovkiRepository extends JpaRepository<ProductSeriesKomplektovkiEntity, Integer> {

    /**
     * ПОЛУЧИТЬ СЕРИИ ДЛЯ ЭЛЕМЕНТА СТРУКТУРЫ КВ
     */
    @Query("SELECT s FROM ProductSeriesKomplektovkiEntity s WHERE s.ssid = :structureId")
    List<ProductSeriesKomplektovkiEntity> findByStructureId(@Param("structureId") Integer structureId);

    /**
     * УДАЛИТЬ ВСЕ СЕРИИ ДЛЯ СТРУКТУРЫ КВ
     */
    @Modifying
    @Query("DELETE FROM ProductSeriesKomplektovkiEntity s WHERE s.ssid IN (SELECT st.id FROM StructureKomplektovkiEntity st WHERE st.idKomplektovka = :kvId)")
    void deleteByKomplektovkaId(@Param("kvId") Integer kvId);
}