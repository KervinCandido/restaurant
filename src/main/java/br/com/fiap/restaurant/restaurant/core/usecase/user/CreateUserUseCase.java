package br.com.fiap.restaurant.restaurant.core.usecase.user;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.UserInput;

import java.util.Objects;

public class CreateUserUseCase {

    private final UserGateway userGateway;

    public CreateUserUseCase(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "UserGateway cannot be null.");
    }

    public User execute(UserInput input) {
        Objects.requireNonNull(input, "UserInput cannot be null.");

        if (userGateway.findById(input.uuid()).isPresent()) {
            throw new BusinessException("User already exists.");
        }

        User user = new User(input.uuid(), input.roles());
        return userGateway.save(user);
    }
}
