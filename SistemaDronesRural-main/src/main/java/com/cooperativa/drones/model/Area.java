package com.cooperativa.drones.model;

import java.math.BigDecimal;

public record Area(Long id, String nome, BigDecimal tamanhoHa, String localizacao, String cultivo) {
    public Area {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("nome");
        if (tamanhoHa == null || tamanhoHa.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("tamanho");
        if (localizacao == null || localizacao.isBlank()) throw new IllegalArgumentException("localizacao");
        if (cultivo == null || cultivo.isBlank()) throw new IllegalArgumentException("cultivo");
    }
}