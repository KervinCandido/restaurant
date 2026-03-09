package br.com.fiap.restaurant.restaurant.core.outbound;

import java.util.Set;

public record RestaurantPublicOutput(
    Long id,
    String name,
    AddressOutput address,
    String cuisineType,
    Set<OpeningHoursOutput> openingHours,
    Set<MenuItemOutput> menuItems
) {}
