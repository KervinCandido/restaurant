package br.com.fiap.restaurant.restaurant.core.usecase.user;

import br.com.fiap.restaurant.restaurant.core.exception.UserNotFoundException;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;

import java.util.Objects;
import java.util.UUID;

public class DeleteUserUseCase {

    private final UserGateway userGateway;

    public DeleteUserUseCase(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "UserGateway cannot be null.");
    }

    public void execute(UUID uuid) {
        Objects.requireNonNull(uuid, "UUID cannot be null.");

        if (userGateway.findById(uuid).isEmpty()) {
            throw new UserNotFoundException(uuid);
        }

        userGateway.delete(uuid);
    }
}
