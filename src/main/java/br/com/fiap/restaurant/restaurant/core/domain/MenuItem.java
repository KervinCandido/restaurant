package br.com.fiap.restaurant.restaurant.core.domain;

import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Objects;

public class MenuItem {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Boolean restaurantOnly;
    private final String photoPath;

    public MenuItem(Long id, String name, String description, BigDecimal price, Boolean restaurantOnly, String photoPath) {
        Objects.requireNonNull(name, "name não pode ser nulo.");
        Objects.requireNonNull(price, "price não pode ser nulo.");
        Objects.requireNonNull(restaurantOnly, "restaurantOnly não pode ser nula.");
        Objects.requireNonNull(photoPath, "photoPath não pode ser nulo.");

        if (name.trim().isBlank()) {
            throw new BusinessException("O nome do item não pode ser vazio.");
        }
        if (photoPath.trim().isBlank()) {
            throw new BusinessException("O caminho da foto não pode ser vazio.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O preço deve ser maior que zero.");
        }

        this.id = id;
        this.name = name.strip();
        this.description = description != null ? description.strip() : null;
        this.price = price;
        this.restaurantOnly = restaurantOnly;
        this.photoPath = photoPath.strip();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean getRestaurantOnly() {
        return restaurantOnly;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem that)) return false;

        if (this.id != null || that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        return Objects.equals(this.name, that.name)
                && Objects.equals(this.description, that.description)
                && Objects.equals(this.price, that.price)
                && Objects.equals(this.restaurantOnly, that.restaurantOnly)
                && Objects.equals(this.photoPath, that.photoPath);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : Objects.hash(name, description, price, restaurantOnly, photoPath);
    }
}
