package br.com.fiap.restaurant.restaurant.core.outbound;

import java.util.UUID;

public record UserOutput(
        UUID id,
        String name,
        String username,
        String email,
        AddressOutput address,
        UserTypeOutput userType
) {}
