package com.example.constructorKV.specif.repository;

import com.example.constructorKV.specif.entity.StructureSpecifEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ СО СТРУКТУРОЙ СПЕЦИФИКАЦИИ
 *
 * Работает с таблицей STRUCTURE_SPECIF.
 *
 * @Repository - указывает, что это Spring Data репозиторий
 */
@Repository
public interface StructureSpecifRepository extends JpaRepository<StructureSpecifEntity, Integer> {

    /**
     * ПОЛУЧИТЬ ВСЮ СТРУКТУРУ СПЕЦИФИКАЦИИ
     *
     * @param idSpecif ID спецификации
     * @return список элементов структуры
     */
    @Query("SELECT s FROM StructureSpecifEntity s WHERE s.idSpecif = :idSpecif ORDER BY s.idDivision, s.pos, s.obozn")
    List<StructureSpecifEntity> findBySpecifId(@Param("idSpecif") Integer idSpecif);

    /**
     * ПОЛУЧИТЬ СТРУКТУРУ ПО РАЗДЕЛУ
     *
     * @param idSpecif ID спецификации
     * @param idDivision ID раздела
     * @return список элементов структуры указанного раздела
     */
    @Query("SELECT s FROM StructureSpecifEntity s WHERE s.idSpecif = :idSpecif AND s.idDivision = :idDivision ORDER BY s.pos, s.obozn")
    List<StructureSpecifEntity> findBySpecifIdAndDivision(
            @Param("idSpecif") Integer idSpecif,
            @Param("idDivision") Integer idDivision
    );

    /**
     * ПОЛУЧИТЬ ТОЛЬКО СБОРКИ (ID_DIVISION = 3)
     */
    @Query("SELECT s FROM StructureSpecifEntity s WHERE s.idSpecif = :idSpecif AND s.idDivision = 3 ORDER BY s.pos, s.obozn")
    List<StructureSpecifEntity> findAssembliesBySpecifId(@Param("idSpecif") Integer idSpecif);
}