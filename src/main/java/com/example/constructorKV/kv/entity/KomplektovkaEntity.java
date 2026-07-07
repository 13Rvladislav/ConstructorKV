// src/main/java/com/example/constructorKV/kv/entity/KomplektovkaEntity.java
package com.example.constructorKV.kv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"KOMPLECTOVKA\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KomplektovkaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // <-- ИЗМЕНИТЬ НА IDENTITY
    @Column(name = "\"ID\"")
    private Integer id;

    @Column(name = "\"DESIGN\"", length = 30)
    private String design;

    @Column(name = "\"ID_SZ\"", nullable = false)
    private Integer idSz;

    @Column(name = "\"SBORKI_SZ\"", length = 100)
    private String sborkiSz;

    @Column(name = "\"DATE_CREATE\"")
    private LocalDateTime dateCreate;

    @Column(name = "\"DATE_ARCHIVING\"")
    private LocalDateTime dateArchiving;

    @Column(name = "\"STATUS\"")
    private Short status;

    @Column(name = "\"FORMED_BY\"")
    private Integer formedBy;

    @Column(name = "\"APPROVED_BY\"")
    private Integer approvedBy;

    @Column(name = "\"APPROVED_DATE\"")
    private LocalDateTime approvedDate;

    @Column(name = "\"SL_ZAP_AGREED_DATE\"")
    private LocalDateTime slZapAgreedDate;
}