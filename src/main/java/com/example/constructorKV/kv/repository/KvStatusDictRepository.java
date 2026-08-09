package com.example.constructorKV.kv.repository;

import com.example.constructorKV.kv.entity.KvStatusDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KvStatusDictRepository extends JpaRepository<KvStatusDictEntity, Integer> {

    Optional<KvStatusDictEntity> findByName(String name);
}