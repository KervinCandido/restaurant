package br.com.fiap.restaurant.restaurant.core.gateway;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;

public interface RestaurantGateway {
    boolean existsRestaurantWithName(String restaurantName);
    Restaurant save(Restaurant restaurant);
}
