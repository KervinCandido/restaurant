package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.List;
import java.util.Objects;

public class GetAllMenuItemsByRestaurantUseCase {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public GetAllMenuItemsByRestaurantUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
    }

    public List<MenuItem> execute(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "id cannot be null");
        restaurantGateway.findById(restaurantId).orElseThrow(() -> new BusinessException("Restaurante não encontrado."));
        return menuItemGateway.findByRestaurantId(restaurantId);
    }
}
