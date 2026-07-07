package com.example.constructorKV.tree.repository;

import com.example.constructorKV.tree.entity.IzdelieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IzdelieRepository extends JpaRepository<IzdelieEntity, Integer> {

    @Query("SELECT i FROM IzdelieEntity i WHERE i.idTema = :temaId ORDER BY i.id")
    List<IzdelieEntity> findByTemaId(@Param("temaId") Integer temaId);

    @Query("SELECT i FROM IzdelieEntity i WHERE i.idTema = :temaId AND i.naimIzd = :naimIzd")
    Optional<IzdelieEntity> findByTemaIdAndNaimIzd(
            @Param("temaId") Integer temaId,
            @Param("naimIzd") String naimIzd
    );

    @Query("SELECT i FROM IzdelieEntity i WHERE LOWER(i.naimIzd) LIKE LOWER(CONCAT('%', :naim, '%'))")
    List<IzdelieEntity> findByNaimIzdContainingIgnoreCase(@Param("naim") String naim);
}