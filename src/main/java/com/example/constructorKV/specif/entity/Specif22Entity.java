package com.example.constructorKV.specif.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "СПЕЦИФИКАЦИЯ"
 *
 * Соответствует таблице SPECIF22 в схеме Konstructor.
 * Хранит информацию о спецификациях (сборках, деталях, стандартных изделиях и т.д.)
 *
 * Ключевые поля:
 * - ID_PARENT: определяет иерархию (NULL = корень)
 * - LINK_IMPLEMETATION: ссылка на другую спецификацию (заимствование)
 *
 * @Entity - класс является сущностью JPA
 * @Table - указывает имя таблицы и схему в БД
 * @Data - Lombok: генерирует геттеры, сеттеры, toString, equals, hashCode
 * @NoArgsConstructor - конструктор без параметров (нужен JPA)
 * @AllArgsConstructor - конструктор со всеми параметрами
 */
@Entity
@Table(name = "\"SPECIF22\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specif22Entity {

    /**
     * Первичный ключ (автоинкремент)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    /**
     * ID родительской спецификации (NULL = корневая)
     * Определяет иерархию дерева
     */
    @Column(name = "\"ID_PARENT\"")
    private Integer idParent;

    /**
     * Обозначение спецификации (например, "А100.0000.070")
     * Уникальное, читаемое имя
     */
    @Column(name = "\"OBOZN\"", length = 150)
    private String obozn;

    /**
     * Наименование спецификации
     */
    @Column(name = "\"NAIM\"", length = 200)
    private String naim;

    /**
     * ID темы (из первого дерева)
     * Связь с таблицей TEMA
     */
    @Column(name = "\"ID_TEMA\"")
    private Integer idTema;

    /**
     * ID изделия (из первого дерева)
     * Связь с таблицей IZDELIE
     */
    @Column(name = "\"ID_IZDELIE\"")
    private Integer idIzdelie;

    /**
     * Первичное применение
     */
    @Column(name = "\"PERV_PRIM\"", length = 50)
    private String pervPrim;

    /**
     * Масса (кг)
     */
    @Column(name = "\"MASSA\"")
    private Double massa;

    /**
     * ССЫЛКА НА ЗАИМСТВОВАНИЕ
     *
     * Если не NULL, то эта спецификация заимствует данные из другой спецификации.
     * При построении дерева мы подставляем данные из оригинальной спецификации.
     *
     * Пример: если LINK_IMPLEMETATION = 1, то берём OBOZN и NAIM из ID = 1
     */
    @Column(name = "\"LINK_IMPLEMETATION\"")
    private Integer linkImplementation;

    /**
     * Флаг (для служебных целей)
     *
     * ВНИМАНИЕ: в БД тип SMALLINT (int2), поэтому используем Short
     */
    @Column(name = "\"FLAG\"")
    private Short flag;

    /**
     * Буквы (стадия разработки)
     * Например: "О", "1", "2", "3", "4", "5"
     */
    @Column(name = "\"LETTERS\"", length = 50)
    private String letters;

    /**
     * ID группы (из первого дерева)
     * Связь с таблицей GROUPE
     */
    @Column(name = "\"ID_GROUPE\"")
    private Integer idGroupe;

    /**
     * Маска (для шаблонов)
     * Например: "А100.**.**.****"
     */
    @Column(name = "\"MASKA\"", length = 100)
    private String maska;

    /**
     * Шаблон
     */
    @Column(name = "\"SHABLON\"", length = 100)
    private String shablon;

    /**
     * Группа спецификации (дополнительная классификация)
     */
    @Column(name = "\"GROUPE_SPECIF\"", length = 50)
    private String groupeSpecif;

    /**
     * ID первичного применения
     */
    @Column(name = "\"PERV_PRIM_ID\"")
    private Integer pervPrimId;
}