package br.com.fiap.restaurant.restaurant.core.inbound;

import java.util.Set;
import java.util.UUID;

public record UserInput(UUID uuid, Set<String> roles) {}
