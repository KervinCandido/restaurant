package br.com.fiap.restaurant.restaurant.core.exception;

public class RestaurantNameIsAlreadyInUseException extends BusinessException {
    public RestaurantNameIsAlreadyInUseException() {
        super("Restaurant name is already in use.");
    }
}
