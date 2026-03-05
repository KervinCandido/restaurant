package br.com.fiap.restaurant.restaurant.utils.core;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.inbound.MenuItemInput;

import java.math.BigDecimal;

public class MenuItemBuilder {

    public static MenuItemBuilder builder() {
        return new MenuItemBuilder();
    }

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean restaurantOnly;
    private String photoPath;

    private MenuItemBuilder() {
        this.id = 1L;
        this.name = "Pizza";
        this.description = "Delicious pizza";
        this.price = new BigDecimal("50.00");
        this.restaurantOnly = false;
        this.photoPath = "/images/pizza.jpg";
    }

    public MenuItemBuilder clean() {
        this.name = "Pizza";
        this.description = "Delicious pizza";
        this.price = new BigDecimal("50.00");
        this.restaurantOnly = false;
        this.photoPath = "/images/pizza.jpg";
        return this;
    }

    public MenuItemBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public MenuItemBuilder withoutId() {
        this.id = null;
        return this;
    }

    public MenuItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuItemBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public MenuItemBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public MenuItemBuilder withRestaurantOnly(Boolean restaurantOnly) {
        this.restaurantOnly = restaurantOnly;
        return this;
    }

    public MenuItemBuilder withPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public MenuItem build() {
        return new MenuItem(id, name, description, price, restaurantOnly, photoPath);
    }

    public MenuItemInput buildInput() {
        return new MenuItemInput(name, description, price, restaurantOnly, photoPath);
    }
}
