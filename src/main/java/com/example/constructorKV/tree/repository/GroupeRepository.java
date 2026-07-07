package com.example.constructorKV.tree.repository;

import com.example.constructorKV.tree.entity.GroupeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С ГРУППАМИ
 *
 * Предоставляет методы для доступа к данным таблицы GROUPE.
 *
 * Слой: DATA ACCESS (доступ к данным)
 */
@Repository
public interface GroupeRepository extends JpaRepository<GroupeEntity, Integer> {

    /**
     * ПОИСК ГРУППЫ ПО НАЗВАНИЮ (точное совпадение)
     *
     * @param naim название группы
     * @return Optional с найденной группой
     */
    @Query("SELECT g FROM GroupeEntity g WHERE g.naim = :naim")
    Optional<GroupeEntity> findByNaim(@Param("naim") String naim);

    /**
     * ПОЛУЧЕНИЕ ВСЕХ ГРУПП С СОРТИРОВКОЙ
     *
     * @return список всех групп, отсортированных по ID
     */
    @Query("SELECT g FROM GroupeEntity g ORDER BY g.id")
    List<GroupeEntity> findAllOrdered();

    /**
     * ПОИСК ГРУПП ПО НАЗВАНИЮ (без учёта регистра, частичное совпадение)
     *
     * @param naim часть названия группы
     * @return список групп
     */
    @Query("SELECT g FROM GroupeEntity g WHERE LOWER(g.naim) LIKE LOWER(CONCAT('%', :naim, '%'))")
    List<GroupeEntity> findByNaimContainingIgnoreCase(@Param("naim") String naim);
}