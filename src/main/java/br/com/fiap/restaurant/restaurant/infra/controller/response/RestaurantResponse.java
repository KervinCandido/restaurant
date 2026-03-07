package br.com.fiap.restaurant.restaurant.infra.controller.response;

import java.util.List;
import java.util.UUID;

public record RestaurantResponse (
        Long id,
        String name,
        String cuisineType,
        AddressResponse address,
        UUID owner,
        List<OpeningHoursResponse> openingHours,
        List<MenuItemResponse> menu,
        List<UUID> employees
){}
