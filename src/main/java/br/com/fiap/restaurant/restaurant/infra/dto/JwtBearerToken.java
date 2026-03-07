package br.com.fiap.restaurant.restaurant.infra.dto;

public record JwtBearerToken(String type, String token, Long expiresIn, String scope) {}
