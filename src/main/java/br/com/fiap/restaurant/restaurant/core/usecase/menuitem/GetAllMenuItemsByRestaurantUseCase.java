package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.model.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.roles.ForGettingRoleName;
import br.com.fiap.restaurant.restaurant.core.domain.roles.MenuItemRoles;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.usecase.UseCaseBase;

import java.util.List;
import java.util.Objects;

public class GetAllMenuItemsByRestaurantUseCase extends UseCaseBase<Long, List<MenuItem>> {

    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public GetAllMenuItemsByRestaurantUseCase(
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        super(loggedUserGateway);
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
    }

    @Override
    protected List<MenuItem> doExecute(Long restaurantId) {
        // aqui input já é non-null e role já foi verificada pelo UseCaseBase

        restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado."));

        return menuItemGateway.findByRestaurantId(restaurantId);
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return MenuItemRoles.VIEW_MENU_ITEM;
    }
}
