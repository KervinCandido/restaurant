package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantPublicOutput;

import java.util.stream.Collectors;

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

    public static RestaurantPublicOutput toOutput(Restaurant restaurant) {
        return new RestaurantPublicOutput(
                restaurant.getId(),
                restaurant.getName(),
                AddressPresenter.toOutput(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHours().stream().map(OpeningHoursPresenter::toOutput).collect(Collectors.toSet()),
                restaurant.getMenuItems().stream().map(MenuItemPresenter::toOutput).collect(Collectors.toSet())
        );
    }
}
