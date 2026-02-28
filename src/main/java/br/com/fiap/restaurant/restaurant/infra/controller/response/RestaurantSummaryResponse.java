package br.com.fiap.restaurant.restaurant.infra.controller.response;

import java.util.List;

public record RestaurantSummaryResponse (
    Long id,
    String name,
    String cuisineType,
    AddressResponse address,
    List<MenuItemResponse> menu,
    List<OpeningHoursResponse> openingHours
) {}
