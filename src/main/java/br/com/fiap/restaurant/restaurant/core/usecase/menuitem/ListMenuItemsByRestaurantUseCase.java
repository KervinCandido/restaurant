package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.Objects;

public class ListMenuItemsByRestaurantUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public ListMenuItemsByRestaurantUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null.");
        Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        this.menuItemGateway = menuItemGateway;
        this.restaurantGateway = restaurantGateway;
    }

    public Page<MenuItem> execute(PagedQuery<Long> filter) {
        Objects.requireNonNull(filter, "filter cannot be null.");
        restaurantGateway.findById(filter.filter()).orElseThrow(() -> new BusinessException("Restaurant not found"));
        return menuItemGateway.findByRestaurant(filter);
    }

}
