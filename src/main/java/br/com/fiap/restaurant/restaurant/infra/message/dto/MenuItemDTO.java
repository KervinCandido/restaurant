package br.com.fiap.restaurant.restaurant.infra.message.dto;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;

import java.math.BigDecimal;

public record MenuItemDTO(Long id, String name, BigDecimal price, Boolean restaurantOnly) {
    public MenuItemDTO(MenuItem menuItem) {
        this(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), menuItem.getRestaurantOnly());
    }
}
