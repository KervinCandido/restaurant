package br.com.fiap.restaurant.restaurant.infra.consumer.dto;

import java.util.Set;
import java.util.UUID;

public record UserDTO(UUID uuid, Set<String> roles) {}
