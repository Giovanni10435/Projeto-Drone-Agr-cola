package com.cooperativa.drones.model;

import java.time.LocalDateTime;
import java.util.Set;

public record Mission(Long id, Long droneId, Long areaId, LocalDateTime inicio, LocalDateTime fim, Set<String> sensores, String status) {
    public Mission {
        if (droneId == null || areaId == null) throw new IllegalArgumentException("ids");
        if (inicio == null || fim == null || !fim.isAfter(inicio)) throw new IllegalArgumentException("tempo");
        if (sensores == null || sensores.isEmpty()) throw new IllegalArgumentException("sensores");
        if (status == null || status.isBlank()) throw new IllegalArgumentException("status");
    }
}