package br.com.fiap.restaurant.restaurant.core.exception;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        this("Invalid username or password.");
    }
}
