package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.CreateRestaurantUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreControllersConfig {

    @Bean
    public RestaurantController restaurantController(CreateRestaurantUseCase createRestaurantUseCase){
        return new RestaurantController(createRestaurantUseCase);
    }

}


