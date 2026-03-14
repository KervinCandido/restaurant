package br.com.fiap.restaurant.restaurant.core.controller;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.inbound.UserInput;
import br.com.fiap.restaurant.restaurant.core.outbound.UserOutput;
import br.com.fiap.restaurant.restaurant.core.usecase.user.CreateUserUseCase;
import br.com.fiap.restaurant.restaurant.core.usecase.user.DeleteUserUseCase;
import br.com.fiap.restaurant.restaurant.core.usecase.user.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UserController")
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @InjectMocks
    private UserController userController;

    private UUID uuid;
    private Set<String> roles;
    private UserInput userInput;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        roles = Set.of("ROLE_USER");
        userInput = new UserInput(uuid, roles);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void shouldCreateUserSuccessfully() {
        // Given
        User expectedUser = new User(uuid, roles);
        when(createUserUseCase.execute(userInput)).thenReturn(expectedUser);

        // When
        UserOutput result = userController.createUser(userInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(uuid);
        assertThat(result.roles()).isEqualTo(roles);
        verify(createUserUseCase).execute(userInput);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        // When
        userController.updateUser(userInput);

        // Then
        verify(updateUserUseCase).execute(userInput);
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        // When
        userController.deleteUser(uuid);

        // Then
        verify(deleteUserUseCase).execute(uuid);
    }

    @Test
    @DisplayName("Deve lançar exceção se UserInput for nulo ao criar")
    void shouldThrowExceptionWhenCreateWithNullInput() {
        assertThatThrownBy(() -> userController.createUser(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserInput cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se UserInput for nulo ao atualizar")
    void shouldThrowExceptionWhenUpdateWithNullInput() {
        assertThatThrownBy(() -> userController.updateUser(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserInput cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se UUID for nulo ao deletar")
    void shouldThrowExceptionWhenDeleteWithNullUuid() {
        assertThatThrownBy(() -> userController.deleteUser(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UUID cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao construir com CreateUserUseCase nulo")
    void shouldThrowExceptionWhenCreateUserUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(null, updateUserUseCase, deleteUserUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateUserUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao construir com UpdateUserUseCase nulo")
    void shouldThrowExceptionWhenUpdateUserUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(createUserUseCase, null, deleteUserUseCase))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateUserUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao construir com DeleteUserUseCase nulo")
    void shouldThrowExceptionWhenDeleteUserUseCaseIsNull() {
        assertThatThrownBy(() -> new UserController(createUserUseCase, updateUserUseCase, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DeleteUserUseCase cannot be null.");
    }
}
