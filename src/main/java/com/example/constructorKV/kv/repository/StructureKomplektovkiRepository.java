// src/main/java/com/example/constructorKV/kv/repository/StructureKomplektovkiRepository.java
package com.example.constructorKV.kv.repository;

import com.example.constructorKV.kv.entity.StructureKomplektovkiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StructureKomplektovkiRepository extends JpaRepository<StructureKomplektovkiEntity, Integer> {

    /**
     * ПОЛУЧИТЬ ВСЮ СТРУКТУРУ КВ
     */
    @Query("SELECT s FROM StructureKomplektovkiEntity s WHERE s.idKomplektovka = :kvId ORDER BY s.level, s.id")
    List<StructureKomplektovkiEntity> findByKomplektovkaId(@Param("kvId") Integer kvId);

    /**
     * УДАЛИТЬ ВСЮ СТРУКТУРУ КВ
     */
    @Modifying
    @Query("DELETE FROM StructureKomplektovkiEntity s WHERE s.idKomplektovka = :kvId")
    void deleteByKomplektovkaId(@Param("kvId") Integer kvId);

    /**
     * ПОЛУЧИТЬ КОРНЕВЫЕ ЭЛЕМЕНТЫ КВ
     */
    @Query("SELECT s FROM StructureKomplektovkiEntity s WHERE s.idKomplektovka = :kvId AND s.parent IS NULL")
    List<StructureKomplektovkiEntity> findRootsByKomplektovkaId(@Param("kvId") Integer kvId);
}