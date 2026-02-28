package br.com.fiap.restaurant.restaurant.core.outbound;

import java.util.UUID;

public record UserSummaryOutput(
    UUID id,
    String name
) {}
