package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.outbound.MenuItemOutput;

public class MenuItemPresenter {
    private MenuItemPresenter() {}

    public static MenuItemOutput toOutput(MenuItem menuItem) {
        return new MenuItemOutput(
            menuItem.getId(),
            menuItem.getName(),
            menuItem.getDescription(),
            menuItem.getPrice(),
            menuItem.getRestaurantOnly(),
            menuItem.getPhotoPath()
        );
    }
}
