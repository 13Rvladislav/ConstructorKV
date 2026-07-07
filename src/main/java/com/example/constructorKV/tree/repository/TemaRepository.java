package com.example.constructorKV.tree.repository;

import com.example.constructorKV.tree.entity.TemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * РЕПОЗИТОРИЙ ДЛЯ РАБОТЫ С ТЕМАМИ
 *
 * Предоставляет методы для доступа к данным таблицы TEMA.
 * Использует Spring Data JPA для автоматической реализации.
 *
 * Слой: DATA ACCESS (доступ к данным)
 *
 * @Repository - указывает, что это репозиторий (Spring создаст бин)
 * JpaRepository<T, ID> - предоставляет CRUD методы:
 * - save(), findById(), findAll(), delete(), count() и т.д.
 */
@Repository
public interface TemaRepository extends JpaRepository<TemaEntity, Long> {

    /**
     * ПОЛУЧЕНИЕ ВСЕХ ТЕМ С СОРТИРОВКОЙ
     *
     * Использует JPQL (Java Persistence Query Language).
     * Возвращает все темы, отсортированные по ID.
     *
     * @return список всех тем
     */
    @Query("SELECT t FROM TemaEntity t ORDER BY t.id")
    List<TemaEntity> findAllOrdered();

    /**
     * ПОЛУЧЕНИЕ ВСЕХ ИЗДЕЛИЙ ДЛЯ ТЕМЫ
     *
     * Возвращает все изделия, принадлежащие указанной теме,
     * даже если у них нет групп.
     *
     * Результат: Object[] где:
     * [0] - ID изделия (Integer)
     * [1] - NAIM_IZD (String) - краткое название
     * [2] - NAME (String) - полное название
     * [3] - DATE_ADD (Object) - дата добавления (может быть Instant, Timestamp или LocalDateTime)
     *
     * @param temaId ID темы
     * @return список массивов с данными изделий
     */
    @Query(value = """
        SELECT DISTINCT 
            i."ID",
            i."NAIM_IZD",
            i."NAME",
            i."DATE_ADD"
        FROM "Konstructor"."IZDELIE" i
        WHERE i."ID_TEMA" = :temaId
        ORDER BY i."ID"
        """, nativeQuery = true)
    List<Object[]> findIzdeliaByTemaId(@Param("temaId") Integer temaId);

    /**
     * ПОЛУЧЕНИЕ ИНФОРМАЦИИ ОБ ИЗДЕЛИИ ПО ID
     *
     * Возвращает детальную информацию об изделии.
     *
     * Результат: Object[] где:
     * [0] - NAIM_IZD (String) - краткое название
     * [1] - NAME (String) - полное название
     * [2] - DATE_ADD (Object) - дата добавления
     *
     * @param izdelieId ID изделия
     * @return массив с данными изделия
     */
    @Query(value = """
        SELECT 
            i."NAIM_IZD",
            i."NAME",
            i."DATE_ADD"
        FROM "Konstructor"."IZDELIE" i
        WHERE i."ID" = :izdelieId
        """, nativeQuery = true)
    List<Object[]> findIzdelieById(@Param("izdelieId") Integer izdelieId);

    /**
     * ПОЛУЧЕНИЕ ГРУПП ДЛЯ ТЕМЫ И ИЗДЕЛИЯ
     *
     * Находит все группы, связанные с указанной темой и изделием
     * через таблицу SPECIF22.
     *
     * Результат: Object[] где:
     * [0] - ID группы (Integer)
     * [1] - NAIM группы (String)
     *
     * @param temaId ID темы
     * @param izdelieId ID изделия
     * @return список групп
     */
    @Query(value = """
        SELECT DISTINCT 
            g."ID",
            g."NAIM"
        FROM "Konstructor"."GROUPE" g
        INNER JOIN "Konstructor"."SPECIF22" s ON s."ID_GROUPE" = g."ID"
        WHERE s."ID_TEMA" = :temaId 
          AND s."ID_IZDELIE" = :izdelieId
        ORDER BY g."ID"
        """, nativeQuery = true)
    List<Object[]> findGroupsByTemaAndIzdelie(
            @Param("temaId") Integer temaId,
            @Param("izdelieId") Integer izdelieId
    );

    /**
     * ПОИСК ТЕМ ПО НАЗВАНИЮ (без учёта регистра)
     *
     * Используется для поиска тем в интерфейсе.
     *
     * @param naim часть названия темы
     * @return список тем, содержащих подстроку в названии
     */
    @Query("SELECT t FROM TemaEntity t WHERE LOWER(t.naim) LIKE LOWER(CONCAT('%', :naim, '%'))")
    List<TemaEntity> findByNaimContainingIgnoreCase(@Param("naim") String naim);
}