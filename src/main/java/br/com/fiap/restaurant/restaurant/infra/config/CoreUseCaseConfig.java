package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.*;
import br.com.fiap.restaurant.restaurant.infra.publisher.RestaurantCreatedPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CoreUseCaseConfig {

    /* ============================
       RESTAURANT
       ============================ */

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(
            LoggedUserGateway loggedUserGateway,
            RestaurantGateway restaurantGateway,
            UserGateway userGateway,
            RestaurantCreatedPublisher restaurantCreatedPublisher
    ) {
        return new CreateRestaurantUseCase(loggedUserGateway, restaurantGateway, userGateway, List.of(restaurantCreatedPublisher));
    }

    @Bean
    UpdateRestaurantUseCase updateRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway, UserGateway userGateway) {
        return new UpdateRestaurantUseCase(loggedUserGateway, restaurantGateway, userGateway);
    }

    @Bean
    GetRestaurantByIdUseCase getRestaurantByIdUseCase(RestaurantGateway restaurantGateway) {
        return new GetRestaurantByIdUseCase(restaurantGateway);
    }

    @Bean
    ListRestaurantsUseCase listRestaurantsUseCase(RestaurantGateway restaurantGateway) {
        return new ListRestaurantsUseCase(restaurantGateway);
    }

    @Bean
    DeleteRestaurantUseCase deleteRestaurantUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway) {
        return new DeleteRestaurantUseCase(loggedUserGateway, restaurantGateway);
    }

    @Bean
    ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase(RestaurantGateway restaurantGateway) {
        return new ListRestaurantsByCuisineTypeUseCase(restaurantGateway);
    }

    @Bean
    GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase(LoggedUserGateway loggedUserGateway, RestaurantGateway restaurantGateway) {
        return new GetRestaurantManagementByIdUseCase(restaurantGateway, loggedUserGateway);
    }

    @Bean
    ListRestaurantsPagedUseCase listRestaurantsPagedUseCase(RestaurantGateway restaurantGateway) {
        return new ListRestaurantsPagedUseCase(restaurantGateway);
    }
}

