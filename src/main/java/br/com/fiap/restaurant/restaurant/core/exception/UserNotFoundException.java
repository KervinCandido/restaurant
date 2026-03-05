package br.com.fiap.restaurant.restaurant.core.exception;

import java.util.UUID;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super("User not found.");
    }

    public UserNotFoundException(UUID uuid) {
        super("User %s not found.".formatted(uuid.toString()));
    }
}
