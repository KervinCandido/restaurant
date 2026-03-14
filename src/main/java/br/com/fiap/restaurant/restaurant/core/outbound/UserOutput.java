package br.com.fiap.restaurant.restaurant.core.outbound;

import java.util.Set;
import java.util.UUID;

public record UserOutput(UUID uuid, Set<String> roles) {}
