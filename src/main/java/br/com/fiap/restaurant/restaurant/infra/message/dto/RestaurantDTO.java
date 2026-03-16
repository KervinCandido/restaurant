package br.com.fiap.restaurant.restaurant.infra.message.dto;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;

import java.util.HashSet;
import java.util.Set;

public record RestaurantDTO(Long id, Set<MenuItemDTO> menu) {
    public RestaurantDTO(Restaurant restaurant) {
        this(restaurant.getId(), new HashSet<>(restaurant
                .getMenuItems()
                .stream()
                .map(mi -> new MenuItemDTO(mi.getId(), mi.getName(), mi.getPrice(), mi.getRestaurantOnly(), restaurant.getId()))
                .toList()));
    }
}
