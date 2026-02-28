package br.com.fiap.restaurant.restaurant.core.inbound;

import java.util.Set;

public record UpdateUserTypeInput(Long id, String name, Set<String> roles) {}