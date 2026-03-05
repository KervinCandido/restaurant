package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;

public class RestaurantPresenter {

    private RestaurantPresenter() {}

    public static RestaurantManagementOutput toManagementOutput(Restaurant restaurant) {

        var address = AddressPresenter.toOutput(restaurant.getAddress());
        var openingHours = restaurant.getOpeningHours().stream().map(OpeningHoursPresenter::toOutput).toList();
        var menu = restaurant.getMenuItems().stream().map(MenuItemPresenter::toOutput).toList();
        var employees = restaurant.getEmployees().stream().map(User::getUuid).toList();

        return new RestaurantManagementOutput(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCuisineType(),
                address,
                restaurant.getOwner().getUuid(),
                openingHours,
                menu,
                employees
        );
    }
}
