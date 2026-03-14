package br.com.fiap.restaurant.restaurant.core.usecase.user;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotFoundException;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.UserInput;

import java.util.Objects;

public class UpdateUserUseCase {

    private final UserGateway userGateway;

    public UpdateUserUseCase(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "UserGateway cannot be null.");
    }

    public void execute(UserInput input) {
        Objects.requireNonNull(input, "UserInput cannot be null.");
        User existingUser = userGateway.findById(input.uuid())
                .orElseThrow(() -> new UserNotFoundException(input.uuid()));

        User updatedUser = new User(existingUser.getUuid(), input.roles());
        userGateway.save(updatedUser);
    }
}
