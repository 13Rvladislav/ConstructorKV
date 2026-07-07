// src/main/java/com/example/constructorKV/kv/entity/StructureKomplektovkiEntity.java
package com.example.constructorKV.kv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СТРУКТУРА КОМПЛЕКТОВОЧНОЙ ВЕДОМОСТИ"
 *
 * Таблица: STRUCTURA_KOMPLECTOVKI в схеме Konstructor.
 * Хранит элементы структуры КВ (сборки и их иерархию).
 */
@Entity
@Table(name = "\"STRUCTURA_KOMPLECTOVKI\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StructureKomplektovkiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    @Column(name = "\"ID_KOMLECTOVKA\"", nullable = false)
    private Integer idKomplektovka;

    @Column(name = "\"ID_SPECIF\"", nullable = false)
    private Integer idSpecif;

    @Column(name = "\"DESIGN\"", length = 100)
    private String design;

    @Column(name = "\"NAME\"", length = 100)
    private String name;

    @Column(name = "\"QUANTITY\"")
    private Integer quantity;

    @Column(name = "\"MASSA\"")
    private Double massa;

    @Column(name = "\"ID_DIVISION\"", nullable = false)
    private Integer idDivision;

    @Column(name = "\"MATERIAL\"", length = 150)
    private String material;

    @Column(name = "\"NOTE\"", length = 150)
    private String note;

    @Column(name = "\"PARENT\"")
    private Integer parent;

    @Column(name = "\"LEVEL\"")
    private Integer level;

    @Column(name = "\"ID_ROOT_SPECIF\"", nullable = false)
    private Integer idRootSpecif;

    @Column(name = "\"ID_ORIGINAL_STRUCTURE\"", nullable = false)
    private Integer idOriginalStructure;
}