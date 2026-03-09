package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.Objects;

public class DeleteRestaurantUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final RestaurantGateway restaurantGateway;

    public DeleteRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null.");
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    public void execute(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null.");

        if (!loggedUserGateway.hasRole(Restaurant.DELETE_RESTAURANT)) {
            throw new OperationNotAllowedException();
        }

        var restaurant = restaurantGateway.findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurant not found."));

        User currentUser = loggedUserGateway.requireCurrentUser();
        if (!restaurant.canBeManagedBy(currentUser)) {
            throw new OperationNotAllowedException();
        }

        restaurantGateway.delete(restaurantId);
    }
}
