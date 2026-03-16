package br.com.fiap.restaurant.restaurant.core.event;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;

public record MenuItemEvent(Long restaurantId, MenuItem menuItem) {}
