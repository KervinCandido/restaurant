package br.com.fiap.restaurant.restaurant.core.exception;

public class UserCannotBeRestaurantOwnerException extends BusinessException {

    public UserCannotBeRestaurantOwnerException() {
        super("User cannot be restaurant ownerId.");
    }
}
