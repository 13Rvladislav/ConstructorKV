package com.example.constructorKV.tree.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * СУЩНОСТЬ "ГРУППА"
 *
 * Соответствует таблице GROUPE в схеме Konstructor.
 * Представляет группу, к которой относится спецификация.
 * Например: "Основное изделие", "Макет", "Оснастка" и т.д.
 *
 * Связи:
 * - Группа может быть связана со многими спецификациями (OneToMany -> Specif22Entity)
 *
 * Важно: В таблице GROUPE нет прямой связи с темой или изделием.
 * Связь устанавливается через таблицу SPECIF22.
 *
 * @Entity - класс является сущностью JPA
 */
@Entity
@Table(name = "\"GROUPE\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupeEntity {

    /**
     * Первичный ключ. Автоинкремент.
     * START WITH 7 в БД.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    /**
     * Наименование группы.
     * Например: "Основное изделие", "Макет", "Оснастка".
     * Длина в БД - 50 символов.
     */
    @Column(name = "\"NAIM\"", length = 50)
    private String naim;
}