package br.com.fiap.restaurant.restaurant.core.controller;

import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import br.com.fiap.restaurant.restaurant.core.presenter.RestaurantPresenter;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.CreateRestaurantUseCase;

import java.util.Objects;

public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase) {
        this.createRestaurantUseCase = Objects.requireNonNull(createRestaurantUseCase, "createRestaurantUseCase cannot be null.");
    }

    public RestaurantManagementOutput createRestaurant(CreateRestaurantInput input) {
        Objects.requireNonNull(input, "createRestaurantInput cannot be null.");
        var restaurant = createRestaurantUseCase.execute(input);
        return RestaurantPresenter.toManagementOutput(restaurant);
    }
}
