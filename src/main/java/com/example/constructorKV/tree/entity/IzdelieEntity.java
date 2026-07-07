package com.example.constructorKV.tree.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"IZDELIE\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IzdelieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    @Column(name = "\"ID_TEMA\"", nullable = false)
    private Integer idTema;

    @Column(name = "\"NAIM_IZD\"", length = 50)
    private String naimIzd;

    @Column(name = "\"NAME\"", length = 400)
    private String name;

    @Column(name = "\"DATE_ADD\"")
    private LocalDateTime dateAdd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"ID_TEMA\"", insertable = false, updatable = false)
    private TemaEntity tema;
}