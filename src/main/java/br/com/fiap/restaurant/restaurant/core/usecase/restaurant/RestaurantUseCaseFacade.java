package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateRestaurantInput;

import java.util.*;

public class RestaurantUseCaseFacade {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    private final ListRestaurantsUseCase listRestaurantsUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;
    private final ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase;
    private final GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase;
    private final ListRestaurantsPagedUseCase listRestaurantsPagedUseCase;

    private RestaurantUseCaseFacade(Builder builder) {
        this.createRestaurantUseCase = Objects.requireNonNull(builder.createRestaurantUseCase, "CreateRestaurantUseCase cannot be null.");
        this.updateRestaurantUseCase = Objects.requireNonNull(builder.updateRestaurantUseCase, "UpdateRestaurantUseCase cannot be null.");
        this.getRestaurantByIdUseCase = Objects.requireNonNull(builder.getRestaurantByIdUseCase, "GetByIdRestaurantUseCase cannot be null.");
        this.listRestaurantsUseCase = Objects.requireNonNull(builder.listRestaurantsUseCase, "GetAllRestaurantUseCase cannot be null.");
        this.deleteRestaurantUseCase = Objects.requireNonNull(builder.deleteRestaurantUseCase, "DeleteRestaurantUseCase cannot be null.");
        this.listRestaurantsByCuisineTypeUseCase = Objects.requireNonNull(builder.listRestaurantsByCuisineTypeUseCase, "ListRestaurantsByCuisineTypeUseCase cannot be null.");
        this.getRestaurantManagementByIdUseCase = Objects.requireNonNull(builder.getRestaurantManagementByIdUseCase, "GetRestaurantManagementByIdUseCase cannot be null.");
        this.listRestaurantsPagedUseCase = Objects.requireNonNull(builder.listRestaurantsPagedUseCase, "ListRestaurantsPagedUseCase cannot be null.");
    }

    public Restaurant createRestaurant(CreateRestaurantInput input) {
        return createRestaurantUseCase.execute(input);
    }

    public void updateRestaurant(UpdateRestaurantInput input) {
        updateRestaurantUseCase.execute(input);
    }

    public Optional<Restaurant> findById(Long id) {
        return getRestaurantByIdUseCase.execute(id);
    }

    public Optional<Restaurant> findManagementById(Long id) {
        return getRestaurantManagementByIdUseCase.execute(id);
    }

    public List<Restaurant> findAll() {
        return listRestaurantsUseCase.execute();
    }

    public Page<Restaurant> findAll(PagedQuery<Void> query) {
        return listRestaurantsPagedUseCase.execute(query);
    }

    public Page<Restaurant> findByCuisineType(PagedQuery<String> query) {
        return listRestaurantsByCuisineTypeUseCase.execute(query);
    }

    public void deleteById(Long id) {
        deleteRestaurantUseCase.execute(id);
    }

    public static class Builder {
        private CreateRestaurantUseCase createRestaurantUseCase;
        private UpdateRestaurantUseCase updateRestaurantUseCase;
        private GetRestaurantByIdUseCase getRestaurantByIdUseCase;
        private ListRestaurantsUseCase listRestaurantsUseCase;
        private DeleteRestaurantUseCase deleteRestaurantUseCase;
        private ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase;
        private GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase;
        private ListRestaurantsPagedUseCase listRestaurantsPagedUseCase;

        public Builder createRestaurantUseCase(CreateRestaurantUseCase createRestaurantUseCase) {
            this.createRestaurantUseCase = createRestaurantUseCase;
            return this;
        }

        public Builder updateRestaurantUseCase(UpdateRestaurantUseCase updateRestaurantUseCase) {
            this.updateRestaurantUseCase = updateRestaurantUseCase;
            return this;
        }

        public Builder getRestaurantByIdUseCase(GetRestaurantByIdUseCase getRestaurantByIdUseCase) {
            this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
            return this;
        }

        public Builder listRestaurantsUseCase(ListRestaurantsUseCase listRestaurantsUseCase) {
            this.listRestaurantsUseCase = listRestaurantsUseCase;
            return this;
        }

        public Builder deleteRestaurantUseCase(DeleteRestaurantUseCase deleteRestaurantUseCase) {
            this.deleteRestaurantUseCase = deleteRestaurantUseCase;
            return this;
        }

        public Builder listRestaurantsByCuisineTypeUseCase(ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase) {
            this.listRestaurantsByCuisineTypeUseCase = listRestaurantsByCuisineTypeUseCase;
            return this;
        }

        public Builder getRestaurantManagementByIdUseCase(GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase) {
            this.getRestaurantManagementByIdUseCase = getRestaurantManagementByIdUseCase;
            return this;
        }

        public Builder listRestaurantsPagedUseCase(ListRestaurantsPagedUseCase listRestaurantsPagedUseCase) {
            this.listRestaurantsPagedUseCase = listRestaurantsPagedUseCase;
            return this;
        }

        public RestaurantUseCaseFacade build() {
            return new RestaurantUseCaseFacade(this);
        }
    }
}