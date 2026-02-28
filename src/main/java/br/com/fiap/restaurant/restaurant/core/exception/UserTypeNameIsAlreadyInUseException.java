package br.com.fiap.restaurant.restaurant.core.exception;

public class UserTypeNameIsAlreadyInUseException extends BusinessException {
    public UserTypeNameIsAlreadyInUseException() {
        super("User type name is already in use.");
    }
}
