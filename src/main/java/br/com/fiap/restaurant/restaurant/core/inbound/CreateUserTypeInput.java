package br.com.fiap.restaurant.restaurant.core.inbound;

import java.util.Set;

public record CreateUserTypeInput(String name, Set<String> roles) {}