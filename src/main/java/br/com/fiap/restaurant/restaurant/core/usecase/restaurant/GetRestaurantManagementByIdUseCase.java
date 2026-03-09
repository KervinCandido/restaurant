package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.Objects;
import java.util.Optional;

public class GetRestaurantManagementByIdUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final RestaurantGateway restaurantGateway;

    public GetRestaurantManagementByIdUseCase(
            RestaurantGateway restaurantGateway,
            LoggedUserGateway loggedUserGateway
    ) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    public Optional<Restaurant> execute(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null.");

        if (!loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)) {
            throw new OperationNotAllowedException();
        }

        var restaurant = restaurantGateway.findByIdWithManagement(restaurantId);

        User currentUser = loggedUserGateway.requireCurrentUser();

        if (restaurant.isPresent() && !restaurant.get().canBeManagedBy(currentUser)) {
            throw new OperationNotAllowedException("Access denied. User is neither ownerId nor employee of the restaurant.");
        }

        return restaurant;
    }
}
