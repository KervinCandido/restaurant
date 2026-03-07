package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.usecase.CreateRestaurantUseCase;
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
}

