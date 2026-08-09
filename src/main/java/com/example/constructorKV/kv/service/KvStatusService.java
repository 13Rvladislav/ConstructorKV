package com.example.constructorKV.kv.service;

import com.example.constructorKV.kv.dto.KvStatusDto;
import com.example.constructorKV.kv.entity.KvStatusDictEntity;
import com.example.constructorKV.kv.repository.KvStatusDictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KvStatusService {

    private final KvStatusDictRepository kvStatusDictRepository;

    public List<KvStatusDto> getAllStatuses() {
        return kvStatusDictRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public KvStatusDto getStatusById(Integer id) {
        return kvStatusDictRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public String getStatusNameById(Integer id) {
        return kvStatusDictRepository.findById(id)
                .map(KvStatusDictEntity::getName)
                .orElse(null);
    }

    public KvStatusDto getStatusByName(String name) {
        return kvStatusDictRepository.findByName(name)
                .map(this::toDto)
                .orElse(null);
    }

    public Integer getDefaultStatusId() {
        return 1;
    }

    private KvStatusDto toDto(KvStatusDictEntity entity) {
        return KvStatusDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}