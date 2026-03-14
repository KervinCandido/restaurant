package br.com.fiap.restaurant.restaurant.core.controller;

import br.com.fiap.restaurant.restaurant.core.inbound.UserInput;
import br.com.fiap.restaurant.restaurant.core.outbound.UserOutput;
import br.com.fiap.restaurant.restaurant.core.presenter.UserPresenter;
import br.com.fiap.restaurant.restaurant.core.usecase.user.CreateUserUseCase;
import br.com.fiap.restaurant.restaurant.core.usecase.user.DeleteUserUseCase;
import br.com.fiap.restaurant.restaurant.core.usecase.user.UpdateUserUseCase;

import java.util.Objects;
import java.util.UUID;

public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, UpdateUserUseCase updateUserUseCase, DeleteUserUseCase deleteUserUseCase) {
        this.createUserUseCase = Objects.requireNonNull(createUserUseCase, "CreateUserUseCase cannot be null.");
        this.updateUserUseCase = Objects.requireNonNull(updateUserUseCase, "UpdateUserUseCase cannot be null.");
        this.deleteUserUseCase = Objects.requireNonNull(deleteUserUseCase, "DeleteUserUseCase cannot be null.");
    }

    public UserOutput createUser(UserInput userInput) {
        Objects.requireNonNull(userInput, "UserInput cannot be null.");
        var user = createUserUseCase.execute(userInput);
        return UserPresenter.toOutput(user);
    }

    public void updateUser(UserInput userInput) {
        Objects.requireNonNull(userInput, "UserInput cannot be null.");
        updateUserUseCase.execute(userInput);
    }

    public void deleteUser(UUID uuid) {
        Objects.requireNonNull(uuid, "UUID cannot be null.");
        deleteUserUseCase.execute(uuid);
    }
}
