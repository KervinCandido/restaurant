package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.RestaurantUseCaseFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreControllersConfig {

    @Bean
    public RestaurantController restaurantController(RestaurantUseCaseFacade restaurantUseCaseFacade){
        return new RestaurantController (restaurantUseCaseFacade);
    }

}


