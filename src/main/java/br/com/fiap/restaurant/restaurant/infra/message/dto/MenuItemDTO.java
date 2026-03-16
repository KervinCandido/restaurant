package br.com.fiap.restaurant.restaurant.infra.message.dto;

import br.com.fiap.restaurant.restaurant.core.event.MenuItemEvent;

import java.math.BigDecimal;

public record MenuItemDTO(Long id, String name, BigDecimal price, Boolean restaurantOnly, Long restaurantId) {
    public MenuItemDTO(MenuItemEvent menuItemEvent) {
        this(menuItemEvent.menuItem().getId(), menuItemEvent.menuItem().getName(), menuItemEvent.menuItem().getPrice(),
                menuItemEvent.menuItem().getRestaurantOnly(), menuItemEvent.restaurantId());
    }
}
