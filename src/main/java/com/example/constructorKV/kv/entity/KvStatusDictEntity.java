package com.example.constructorKV.kv.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"KV_STATUS_DICT\"", schema = "\"Konstructor\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KvStatusDictEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    private Integer id;

    @Column(name = "\"NAME\"", length = 50, nullable = false)
    private String name;

    @Column(name = "\"DESCRIPTION\"", length = 200)
    private String description;
}