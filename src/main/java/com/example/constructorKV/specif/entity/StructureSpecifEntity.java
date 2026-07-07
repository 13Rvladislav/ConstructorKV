package com.example.constructorKV.specif.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СТРУКТУРА СПЕЦИФИКАЦИИ"
 *
 * Таблица: STRUCTURE_SPECIF в схеме Konstructor.
 *
 * @Entity - класс является сущностью JPA
 */
@Entity
@Table(name = "\"STRUCTURE_SPECIF\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StructureSpecifEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    @Column(name = "\"ID_SPECIF\"")
    private Integer idSpecif;

    @Column(name = "\"ZONE\"", length = 11)
    private String zone;

    @Column(name = "\"FORMAT\"", length = 11)
    private String format;

    @Column(name = "\"POS\"", length = 11)
    private String pos;

    @Column(name = "\"OBOZN\"", length = 200)
    private String obozn;

    @Column(name = "\"NAIM\"", length = 250)
    private String naim;

    @Column(name = "\"QUANTITY\"")
    private Integer quantity;

    @Column(name = "\"ID_DIVISION\"")
    private Short idDivision;

    @Column(name = "\"RMASSA\"")
    private Double massa;

    @Column(name = "\"MATERIAL\"", length = 150)
    private String material;

    @Column(name = "\"FLAG_PRODUCT_SERIES\"")
    private Boolean flagProductSeries;

    @Column(name = "\"ID_NOMENKLATURA\"")
    private Integer idNomenklatura;

    @Column(name = "\"ID_SPECIF_22\"")
    private Integer idSpecif22;
}