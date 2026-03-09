package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreControllersConfig {

    @Bean
    public RestaurantController restaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                                     UpdateRestaurantUseCase updateRestaurantUseCase,
                                                     GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                                     ListRestaurantsUseCase listRestaurantsUseCase,
                                                     DeleteRestaurantUseCase deleteRestaurantUseCase,
                                                     ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase,
                                                     GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase,
                                                     ListRestaurantsPagedUseCase listRestaurantsPagedUseCase){
        return new RestaurantController (
            createRestaurantUseCase,
            updateRestaurantUseCase,
            getRestaurantByIdUseCase,
            listRestaurantsUseCase,
            deleteRestaurantUseCase,
            listRestaurantsByCuisineTypeUseCase,
            getRestaurantManagementByIdUseCase,
            listRestaurantsPagedUseCase
        );
    }

}


