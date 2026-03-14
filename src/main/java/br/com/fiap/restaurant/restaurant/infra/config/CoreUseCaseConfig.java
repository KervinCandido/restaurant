package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.usecase.menuitem.*;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.*;
import br.com.fiap.restaurant.restaurant.core.usecase.user.CreateUserUseCase;
import br.com.fiap.restaurant.restaurant.core.usecase.user.DeleteUserUseCase;
import br.com.fiap.restaurant.restaurant.core.usecase.user.UpdateUserUseCase;
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

    @Bean
    public RestaurantUseCaseFacade restaurantUseCaseFacade(CreateRestaurantUseCase createRestaurantUseCase,
                                                           UpdateRestaurantUseCase updateRestaurantUseCase,
                                                           GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                                           ListRestaurantsUseCase listRestaurantsUseCase,
                                                           DeleteRestaurantUseCase deleteRestaurantUseCase,
                                                           ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase,
                                                           GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase,
                                                           ListRestaurantsPagedUseCase listRestaurantsPagedUseCase){
        return new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase)
                .build();
    }

    @Bean
    CreateMenuItemUseCase createMenuItemUseCase(LoggedUserGateway loggedUserGateway, MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        return new CreateMenuItemUseCase(loggedUserGateway, menuItemGateway, restaurantGateway);
    }

    @Bean
    DeleteMenuItemUseCase deleteMenuItemUseCase(LoggedUserGateway loggedUserGateway, MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        return new DeleteMenuItemUseCase(loggedUserGateway, menuItemGateway, restaurantGateway);
    }

    @Bean
    GetAllMenuItemsByRestaurantUseCase getAllMenuItemsByRestaurantUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        return new GetAllMenuItemsByRestaurantUseCase(menuItemGateway, restaurantGateway);
    }

    @Bean
    GetMenuItemByIdUseCase getMenuItemByIdUseCase(MenuItemGateway menuItemGateway) {
        return new GetMenuItemByIdUseCase(menuItemGateway);
    }

    @Bean
    ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase(MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        return new ListMenuItemsByRestaurantUseCase(menuItemGateway, restaurantGateway);
    }

    @Bean
    UpdateMenuItemUseCase updateMenuItemUseCase(LoggedUserGateway loggedUserGateway, MenuItemGateway menuItemGateway, RestaurantGateway restaurantGateway) {
        return new UpdateMenuItemUseCase(loggedUserGateway, menuItemGateway, restaurantGateway);
    }

    @Bean
    CreateUserUseCase createUserUseCase(UserGateway userGateway) {
        return new CreateUserUseCase(userGateway);
    }

    @Bean
    UpdateUserUseCase updateUserUseCase(UserGateway userGateway) {
        return new UpdateUserUseCase(userGateway);
    }

    @Bean
    DeleteUserUseCase deleteUserUseCase(UserGateway userGateway) {
        return new DeleteUserUseCase(userGateway);
    }
}

