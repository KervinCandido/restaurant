package br.com.fiap.restaurant.restaurant.core.outbound;

import java.util.List;
import java.util.UUID;

public record RestaurantManagementOutput (
        Long id,
        String name,
        String cuisineType,
        AddressOutput address,
        UUID owner,
        List<OpeningHoursOutput> openingHours,
        List<MenuItemOutput> menu,
        List<UUID> employees) {}
