package com.example.constructorKV.slzap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СВЯЗЬ СБОРКИ И СЛУЖЕБНОЙ ЗАПИСКИ"
 *
 * Таблица: SBORKI_SL_ZAP в схеме Konstructor.
 *
 * Назначение: связывает спецификации (сборки) со служебными записками.
 *
 * Связи:
 * - ID_IZDEL_SL_ZAP → IZDEL_SL_ZAP.ID (связь изделия и служебной записки)
 * - ID_SL_ZAP       → SL_ZAPISKA.ID   (служебная записка)
 * - ID_SPECIF       → SPECIF22.ID     (спецификация, может быть несколько через запятую)
 *
 * @Entity - класс является сущностью JPA
 */
@Entity
@Table(name = "\"SBORKI_SL_ZAP\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SborkiSlZapEntity {

    /** Первичный ключ (автоинкремент) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    /**
     * ID связи изделия и служебной записки
     * Ссылка на IZDEL_SL_ZAP.ID
     */
    @Column(name = "\"ID_IZDEL_SL_ZAP\"")
    private Integer idIzdelSlZap;

    /** ID служебной записки */
    @Column(name = "\"ID_SL_ZAP\"")
    private Integer idSlZap;

    /**
     * ID спецификации (может быть несколько через запятую)
     * Например: "3,4,9" или "3"
     *
     * ВАЖНО: это поле хранит строку с ID, а не внешний ключ.
     * Поэтому используем поиск через POSITION в SQL-запросах.
     */
    @Column(name = "\"ID_SPECIF\"", length = 200)
    private String idSpecif;
}