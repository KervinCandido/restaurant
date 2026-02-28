package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.model.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.roles.ForGettingRoleName;
import br.com.fiap.restaurant.restaurant.core.domain.roles.RestaurantRoles;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.usecase.UseCaseWithoutInput;

import java.util.List;
import java.util.Objects;

public class ListRestaurantsUseCase extends UseCaseWithoutInput<List<Restaurant>> {

    private final RestaurantGateway restaurantGateway;

    public ListRestaurantsUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway) {
        super(Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null."));
        this.restaurantGateway = Objects.requireNonNull(restaurantGateway, "RestaurantGateway cannot be null.");
    }

    @Override
    protected List<Restaurant> doExecute() {
        return restaurantGateway.findAll();
    }

    @Override
    protected ForGettingRoleName getRequiredRole() {
        return RestaurantRoles.VIEW_RESTAURANT;
    }

}
