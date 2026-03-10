package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.Objects;

public class DeleteMenuItemUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;

    public DeleteMenuItemUseCase(
            LoggedUserGateway loggedUserGateway,
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway
    ) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null.");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    public Void execute(Long id) {
        Objects.requireNonNull(id, "id cannot be null");

        if (!loggedUserGateway.hasRole(MenuItem.DELETE_MENU_ITEM)) {
            throw new OperationNotAllowedException();
        }

        var restaurantId = menuItemGateway.findRestaurantIdByItemId(id)
                .orElseThrow(() -> new BusinessException("Restaurante associado não encontrado"));

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + restaurantId));

        User currentUser = loggedUserGateway.requireCurrentUser();

        var ownerId = restaurant.getOwner() != null ? restaurant.getOwner().getUuid() : null;
        var currentId = currentUser != null ? currentUser.getUuid() : null;

        if (ownerId == null || currentId == null || !ownerId.equals(currentId)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode deletar itens do cardápio.");
        }

        menuItemGateway.deleteById(id);
        return null;
    }
}
