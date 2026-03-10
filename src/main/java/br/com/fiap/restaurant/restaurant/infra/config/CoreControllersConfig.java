package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.controller.MenuItemController;
import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.core.usecase.menuitem.*;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.RestaurantUseCaseFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreControllersConfig {

    @Bean
    public RestaurantController restaurantController(RestaurantUseCaseFacade restaurantUseCaseFacade){
        return new RestaurantController (restaurantUseCaseFacade);
    }

    @Bean
    public MenuItemController menuItemController(ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase,
                                                 CreateMenuItemUseCase createMenuItemUseCase,
                                                 UpdateMenuItemUseCase updateMenuItemUseCase,
                                                 DeleteMenuItemUseCase deleteMenuItemUseCase,
                                                 GetMenuItemByIdUseCase getMenuItemByIdUseCase) {
        return new MenuItemController (
            listMenuItemsByRestaurantUseCase,
            createMenuItemUseCase,
            updateMenuItemUseCase,
            deleteMenuItemUseCase,
            getMenuItemByIdUseCase
        );
    }

}


