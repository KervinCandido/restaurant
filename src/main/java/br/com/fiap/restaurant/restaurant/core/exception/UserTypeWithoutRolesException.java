package br.com.fiap.restaurant.restaurant.core.exception;

public class UserTypeWithoutRolesException extends BusinessException {
    public UserTypeWithoutRolesException() {
        super("User type must have at least one role valid.");
    }
}
