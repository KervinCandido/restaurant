package br.com.fiap.restaurant.restaurant.core.controller;

import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantPublicOutput;
import br.com.fiap.restaurant.restaurant.core.presenter.RestaurantPresenter;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.RestaurantUseCaseFacade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RestaurantController {

    private static final String RESTAURANT_ID_CANNOT_BE_NULL = "Restaurant Id cannot be null.";

    private final RestaurantUseCaseFacade useCases;

    public RestaurantController(RestaurantUseCaseFacade useCases) {
        this.useCases = Objects.requireNonNull(useCases, "RestaurantUseCaseFacade cannot be null.");
    }

    public RestaurantManagementOutput createRestaurant(CreateRestaurantInput createRestaurantInput) {
        Objects.requireNonNull(createRestaurantInput, "CreateRestaurantInput cannot be null.");
        var restaurant = useCases.createRestaurant(createRestaurantInput);
        return RestaurantPresenter.toManagementOutput(restaurant);
    }

    public void updateRestaurant(UpdateRestaurantInput updateRestaurantInput) {
        Objects.requireNonNull(updateRestaurantInput, "UpdateRestaurantInput cannot be null.");
        useCases.updateRestaurant(updateRestaurantInput);
    }

    public Optional<RestaurantPublicOutput> findById(Long restaurantId) {
        Objects.requireNonNull(restaurantId, RESTAURANT_ID_CANNOT_BE_NULL);
        var restaurant = useCases.findById(restaurantId);
        return restaurant.map(RestaurantPresenter::toOutput);
    }

    public Optional<RestaurantManagementOutput> findManagementById(Long restaurantId) {
        Objects.requireNonNull(restaurantId, RESTAURANT_ID_CANNOT_BE_NULL);
        var restaurant = useCases.findManagementById(restaurantId);
        return restaurant.map(RestaurantPresenter::toManagementOutput);
    }

    // Mantido por compatibilidade
    public List<RestaurantPublicOutput> findAll() {
        return useCases.findAll()
                .stream()
                .map(RestaurantPresenter::toOutput)
                .toList();
    }

    public Page<RestaurantPublicOutput> findAll(int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<Void>(null, pageNumber, pageSize);
        var page = useCases.findAll(pagedQuery);
        return page.mapItems(RestaurantPresenter::toOutput);
    }

    public Page<RestaurantPublicOutput> findByCuisineType(String cuisineType, int pageNumber, int pageSize) {
        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);
        var page = useCases.findByCuisineType(pagedQuery);
        return page.mapItems(RestaurantPresenter::toOutput);
    }

    public void deleteById(Long restaurantId) {
        Objects.requireNonNull(restaurantId, RESTAURANT_ID_CANNOT_BE_NULL);
        useCases.deleteById(restaurantId);
    }
}
