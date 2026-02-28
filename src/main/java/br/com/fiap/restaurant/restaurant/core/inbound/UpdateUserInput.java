package br.com.fiap.restaurant.restaurant.core.inbound;

import java.util.UUID;

public record UpdateUserInput(
    UUID id,
    String name,
    String username,
    String email,
    AddressInput address,
    Long userTypeId
){}

