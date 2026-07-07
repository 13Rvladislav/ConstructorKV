// src/main/java/com/example/constructorKV/kv/entity/ProductSeriesKomplektovkiEntity.java
package com.example.constructorKV.kv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СЕРИИ ДЛЯ СТРУКТУРЫ КВ"
 *
 * Таблица: PRODUCT_SERIES_KOMPLEKTOVKI в схеме Konstructor.
 * Хранит серии для элементов структуры комплектовочной ведомости.
 *
 * ВНИМАНИЕ: Если таблицы PRODUCT_SERIES_KOMPLEKTOVKI нет в БД,
 * используем таблицу PRODUCT_SERIES с SSID = ID из STRUCTURA_KOMPLECTOVKI.
 */
@Entity
@Table(name = "\"PRODUCT_SERIES\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeriesKomplektovkiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    /** ID элемента структуры КВ (ссылка на STRUCTURA_KOMPLECTOVKI.ID) */
    @Column(name = "\"SSID\"")
    private Integer ssid;

    /** Начало диапазона серии */
    @Column(name = "\"MINI\"")
    private Integer mini;

    /** Конец диапазона серии */
    @Column(name = "\"MAXI\"")
    private Integer maxi;

    /** Количество */
    @Column(name = "\"QUANTITY\"")
    private Integer quantity;

    /** Масса */
    @Column(name = "\"MASSA\"")
    private Double massa;

    /** Габариты */
    @Column(name = "\"DIMENSIONS\"", length = 100)
    private String dimensions;
}