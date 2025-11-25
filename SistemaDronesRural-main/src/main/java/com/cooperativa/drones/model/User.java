package com.cooperativa.drones.model;

public record User(Long id, String username, String passwordHash, Role role) {}