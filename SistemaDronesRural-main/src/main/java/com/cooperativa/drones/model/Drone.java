package com.cooperativa.drones.model;

import java.util.Set;

public record Drone(Long id, String codigo, Set<String> sensores, String status, int bateria) {
    public Drone {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("codigo");
        if (sensores == null || sensores.isEmpty()) throw new IllegalArgumentException("sensores");
        if (status == null || status.isBlank()) throw new IllegalArgumentException("status");
        if (bateria < 0 || bateria > 100) throw new IllegalArgumentException("bateria");
    }
}