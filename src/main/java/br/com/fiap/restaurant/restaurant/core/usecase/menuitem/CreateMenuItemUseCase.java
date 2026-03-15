package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateMenuItemInput;

import java.util.Objects;
import java.util.UUID;

public class CreateMenuItemUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final PublisherGateway<Restaurant> updateRestaurantPublisher;

    public CreateMenuItemUseCase(
            LoggedUserGateway loggedUserGateway,
            MenuItemGateway menuItemGateway,
            RestaurantGateway restaurantGateway,
            PublisherGateway<Restaurant> updateRestaurantPublisher
    ) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null.");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
        this.updateRestaurantPublisher = Objects.requireNonNull(updateRestaurantPublisher, "createRestaurantPublisher cannot be null.");
    }

    public MenuItem execute(CreateMenuItemInput input) {
        Objects.requireNonNull(input, "createRestaurantInput cannot be null.");

        if (!loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)) {
            throw new OperationNotAllowedException();
        }

        // Base já valida input != null (Input cannot be null.)
        Long restaurantId = Objects.requireNonNull(input.restaurantId(), "restaurantId cannot be null.");

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado com ID: " + restaurantId));

        String name = Objects.requireNonNull(input.name(), "name cannot be null").trim();
        if (name.isBlank()) {
            throw new BusinessException("name cannot be blank");
        }

        String description = input.description() != null ? input.description().trim() : null;
        String photoPath = input.photoPath() != null ? input.photoPath().trim() : null;

        User currentUser = loggedUserGateway.requireCurrentUser();

        // compara por id (mais robusto que equals de instância)
        UUID ownerId = restaurant.getOwner() != null ? restaurant.getOwner().getUuid() : null;
        UUID currentUserId = currentUser != null ? currentUser.getUuid() : null;

        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode criar itens do cardápio.");
        }

        if (menuItemGateway.existsByNameAndRestaurantId(name, restaurantId)) {
            throw new BusinessException(
                    "Já existe um item de cardápio com o nome '%s' no restaurante '%s'."
                            .formatted(name, restaurant.getName())
            );
        }

        MenuItem menuItem = new MenuItem(
                null,
                name,
                description,
                input.price(),
                input.restaurantOnly(),
                photoPath
        );

        MenuItem saved = menuItemGateway.save(menuItem, restaurantId);
        restaurant.addMenuItem(saved);

        updateRestaurantPublisher.publish(restaurant);
        return saved;
    }
}
