package com.example.constructorKV.tree.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * СУЩНОСТЬ "ТЕМА"
 *
 * Соответствует таблице TEMA в схеме Konstructor.
 * Представляет тему разработки (например, "А-100", "Бе-200" и т.д.)
 *
 * Связи:
 * - Одна тема содержит много изделий (OneToMany -> IzdelieEntity)
 * - В таблице SPECIF22 есть ссылка на тему
 *
 * @Entity - класс является сущностью JPA
 * @Table - указывает имя таблицы и схему в БД
 * @Data - Lombok: генерирует геттеры, сеттеры, toString, equals, hashCode
 * @NoArgsConstructor - конструктор без параметров (нужен JPA)
 * @AllArgsConstructor - конструктор со всеми параметрами
 */
@Entity
@Table(name = "\"TEMA\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemaEntity {

    /**
     * Первичный ключ. Автоинкремент через IDENTITY.
     * START WITH 1 в БД.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Long id;

    /**
     * Наименование темы (например, "А-100", "70М2").
     * Длина в БД - 50 символов.
     */
    @Column(name = "\"NAIM\"", length = 50)
    private String naim;

    /**
     * Индекс КД (Конструкторской Документации).
     * Может быть null, если не указан.
     */
    @Column(name = "\"INDEKSKD\"")
    private Integer indeksKd;

    /**
     * Список изделий, принадлежащих теме.
     * FetchType.LAZY - данные загружаются только при обращении (ленивая загрузка).
     * mappedBy = "tema" - поле в IzdelieEntity, которое владеет связью.
     * CascadeType.ALL - все операции каскадируются на дочерние сущности.
     * orphanRemoval = true - при удалении темы удаляются и её изделия.
     */
    @OneToMany(mappedBy = "tema", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IzdelieEntity> izdelia = new ArrayList<>();

    /**
     * Удобный метод для добавления изделия к теме.
     * Автоматически устанавливает связь с обоих сторон.
     *
     * @param izdelie изделие для добавления
     */
    public void addIzdelie(IzdelieEntity izdelie) {
        if (izdelia == null) {
            izdelia = new ArrayList<>();
        }
        izdelia.add(izdelie);
        izdelie.setTema(this);
    }

    /**
     * Удобный метод для удаления изделия из темы.
     *
     * @param izdelie изделие для удаления
     */
    public void removeIzdelie(IzdelieEntity izdelie) {
        if (izdelia != null) {
            izdelia.remove(izdelie);
            izdelie.setTema(null);
        }
    }
}