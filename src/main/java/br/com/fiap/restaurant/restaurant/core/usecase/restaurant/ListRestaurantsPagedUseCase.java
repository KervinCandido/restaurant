package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;

import java.util.Objects;

public class ListRestaurantsPagedUseCase {

    private final RestaurantGateway restaurantGateway;

    public ListRestaurantsPagedUseCase(RestaurantGateway restaurantGateway) {
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    public Page<Restaurant> execute(PagedQuery<Void> query) {
        Objects.requireNonNull(query, "query cannot be null.");
        return restaurantGateway.findAll(query);
    }
}
