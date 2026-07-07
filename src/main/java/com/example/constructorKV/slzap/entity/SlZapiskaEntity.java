package com.example.constructorKV.slzap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СЛУЖЕБНАЯ ЗАПИСКА"
 *
 * Таблица: SL_ZAPISKA в схеме Konstructor.
 *
 * Назначение: хранит информацию о служебных записках.
 *
 * @Entity - класс является сущностью JPA
 */
@Entity
@Table(name = "\"SL_ZAPISKA\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlZapiskaEntity {

    /** Первичный ключ (автоинкремент) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    /** Кому адресовано */
    @Column(name = "\"ADDRESSED\"", length = 50)
    private String addressed;

    /** Основание */
    @Column(name = "\"FOOTING\"", length = 100)
    private String footing;

    /** Наименование */
    @Column(name = "\"NAIM\"", length = 100)
    private String naim;

    /** Содержание */
    @Column(name = "\"CONTENT\"", length = 100)
    private String content;

    /** Примечание */
    @Column(name = "\"NOTE\"", length = 200)
    private String note;

    /** Согласовано */
    @Column(name = "\"AGREED\"", length = 200)
    private String agreed;

    /** Подписи (кто подписал) */
    @Column(name = "\"SIGNATORIES\"", length = 200)
    private String signatories;

    /** Обозначение служебной записки (уникальное) */
    @Column(name = "\"OBOZN\"", length = 100)
    private String obozn;

    /** Наименование служебной записки */
    @Column(name = "\"NAIM_SL_ZAP\"", length = 100)
    private String naimSlZap;

    /** ID сотрудника, который разработал */
    @Column(name = "\"DEVELOPED\"")
    private Integer developed;

    /** ID сотрудника, который проверил */
    @Column(name = "\"CHECKED_IT_OUT\"")
    private Integer checkedItOut;

    /** ID сотрудника - начальник отдела */
    @Column(name = "\"HEAD_DEPARTMENT\"")
    private Integer headDepartment;

    /** ID сотрудника - нормоконтроль */
    @Column(name = "\"NORM_CONTROL\"")
    private Integer normControl;

    /** ID сотрудника, который утвердил */
    @Column(name = "\"APPROVED\"")
    private Integer approved;

    /**
     * Статус флага
     * 0 - Активна
     * 1 - В работе
     * 2 - Закрыта
     * 3 - На согласовании
     * 4 - Согласована
     * 5 - Отклонена
     */
    @Column(name = "\"FLAG_STATUS\"")
    private Short flagStatus;

    /** Тип флага (для дополнительной классификации) */
    @Column(name = "\"FLAG_TYPE\"")
    private Short flagType;

    /** ID состояния согласования (ссылка на STATE_SOGLAS) */
    @Column(name = "\"SOST_SOGL_ID\"")
    private Integer sostSoglId;
}