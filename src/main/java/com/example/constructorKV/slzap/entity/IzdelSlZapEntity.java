package com.example.constructorKV.slzap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СВЯЗЬ ИЗДЕЛИЯ И СЛУЖЕБНОЙ ЗАПИСКИ"
 *
 * Таблица: IZDEL_SL_ZAP в схеме Konstructor.
 *
 * Назначение: связывает служебные записки с конкретными изделиями.
 *
 * Связи:
 * - ID_SL_ZAP → SL_ZAPISKA.ID (служебная записка)
 * - ID_IZD    → IZDELIE.ID   (изделие, сущность из пакета tree.entity)
 *
 * ВАЖНО: IzdelieEntity НЕ ДУБЛИРУЕМ! Используем существующий из tree.entity.
 *
 * @Entity - класс является сущностью JPA
 * @Table - указывает имя таблицы и схему в БД
 * @Data - Lombok: геттеры, сеттеры, toString, equals, hashCode
 * @NoArgsConstructor - конструктор без параметров (нужен JPA)
 * @AllArgsConstructor - конструктор со всеми параметрами
 */
@Entity
@Table(name = "\"IZDEL_SL_ZAP\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IzdelSlZapEntity {

    /** Первичный ключ (автоинкремент) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    /**
     * ID служебной записки
     * Ссылка на SL_ZAPISKA.ID
     */
    @Column(name = "\"ID_SL_ZAP\"")
    private Integer idSlZap;

    /**
     * ID изделия
     * Ссылка на IZDELIE.ID (сущность из tree.entity)
     *
     * ВНИМАНИЕ: Это внешний ключ к таблице IZDELIE.
     * НЕ создаём здесь @ManyToOne, чтобы не создавать циклических зависимостей.
     * Данные об изделии получаем через отдельный запрос или LEFT JOIN в репозитории.
     */
    @Column(name = "\"ID_IZD\"")
    private Integer idIzd;

    /** Условие (например, "Закрыто частично") */
    @Column(name = "\"CONDITION\"", length = 20)
    private String condition;

    /** Стадия работы */
    @Column(name = "\"STAGE_OF_WORK\"", length = 100)
    private String stageOfWork;

    /** Код ОКБ работы */
    @Column(name = "\"OKB_WORK_CODE\"", length = 100)
    private String okbWorkCode;

    /** Заказ */
    @Column(name = "\"ZAKAZ\"", length = 100)
    private String zakaz;
}