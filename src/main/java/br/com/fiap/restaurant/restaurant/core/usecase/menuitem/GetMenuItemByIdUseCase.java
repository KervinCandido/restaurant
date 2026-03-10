package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;

import java.util.Objects;
import java.util.Optional;

public class GetMenuItemByIdUseCase {

    private final MenuItemGateway menuItemGateway;

    public GetMenuItemByIdUseCase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
    }

    public Optional<MenuItem> execute(Long id) {
        Objects.requireNonNull(id, "id cannot be null");
        return menuItemGateway.findById(id);
    }
}
