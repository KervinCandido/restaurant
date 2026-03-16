package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.event.MenuItemEvent;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateMenuItemInput;

import java.util.Objects;

public class UpdateMenuItemUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final MenuItemGateway menuItemGateway;
    private final RestaurantGateway restaurantGateway;
    private final PublisherGateway<MenuItemEvent> updateMenuItemPublisher;

    public UpdateMenuItemUseCase(LoggedUserGateway loggedUserGateway, MenuItemGateway menuItemGateway,
                                 RestaurantGateway restaurantGateway, PublisherGateway<MenuItemEvent> updateMenuItemPublisher) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "MenuItemGateway cannot be null");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null");
        this.updateMenuItemPublisher = Objects.requireNonNull(updateMenuItemPublisher, "updateMenuItemPublisher cannot be null.");
    }

    public MenuItem execute(UpdateMenuItemInput input) {
        Objects.requireNonNull(input, "UpdateMenuItemInput cannot be null");

        if (!loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)) {
            throw new OperationNotAllowedException();
        }

        Long itemId = Objects.requireNonNull(input.id(), "id cannot be null");

        MenuItem existingItem = menuItemGateway.findById(itemId)
                .orElseThrow(() -> new BusinessException("Item de cardápio não encontrado com ID: " + itemId));

        Long restaurantId = menuItemGateway.findRestaurantIdByItemId(itemId)
                .orElseThrow(() -> new BusinessException("Restaurante associado não encontrado"));

        Restaurant restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado"));

        // valida se é o dono
        User currentUser = loggedUserGateway.requireCurrentUser();
        var ownerId = restaurant.getOwner() != null ? restaurant.getOwner().getUuid() : null;
        var currentUserId = currentUser.getUuid();

        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new OperationNotAllowedException("Apenas o dono do restaurante pode atualizar itens do cardápio.");
        }

        String newName = Objects.requireNonNull(input.name(), "name cannot be null").trim();
        String oldName = existingItem.getName() != null ? existingItem.getName().trim() : null;

        if (!newName.equals(oldName) && menuItemGateway.existsByNameAndRestaurantId(newName, restaurantId)) {
            throw new BusinessException("Já existe um item com este nome no restaurante");
        }

        MenuItem updatedItem = new MenuItem(
                existingItem.getId(),
                newName,
                input.description() != null ? input.description().trim() : null,
                input.price(),
                input.restaurantOnly(),
                input.photoPath() != null ? input.photoPath().trim() : existingItem.getPhotoPath()
        );

        MenuItem saved = menuItemGateway.save(updatedItem, restaurantId);

        updateMenuItemPublisher.publish(new MenuItemEvent(restaurantId, saved));
        return saved;
    }
}
