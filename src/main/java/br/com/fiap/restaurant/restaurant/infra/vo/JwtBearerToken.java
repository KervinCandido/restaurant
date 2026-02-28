package br.com.fiap.restaurant.restaurant.infra.vo;

public record JwtBearerToken(String type, String token, Long expiresIn, String scope) {}
