package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.Objects;
import java.util.Optional;

public class GetRestaurantByIdUseCase {

    private final RestaurantGateway restaurantGateway;

    public GetRestaurantByIdUseCase(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    public Optional<Restaurant> execute(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null.");
        return restaurantGateway.findById(restaurantId);
    }
}
