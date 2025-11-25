package com.cooperativa.drones.model;

import java.time.LocalDateTime;

public record Reading(Long id, Long missaoId, String tipo, Double valor, String unidade, String imagemPath, LocalDateTime capturadoEm) {
    public Reading {
        if (missaoId == null) throw new IllegalArgumentException("missao");
        if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("tipo");
        if (capturadoEm == null) throw new IllegalArgumentException("tempo");
    }
}