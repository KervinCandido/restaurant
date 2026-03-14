package br.com.fiap.restaurant.restaurant.core.usecase.user;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotFoundException;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.UserInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateUserUseCase")
class UpdateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    private UUID uuid;
    private Set<String> roles;
    private UserInput userInput;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        roles = Set.of("ROLE_ADMIN");
        userInput = new UserInput(uuid, roles);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUserSuccessfully() {
        // Given
        User existingUser = new User(uuid, Set.of("ROLE_USER"));
        when(userGateway.findById(uuid)).thenReturn(Optional.of(existingUser));

        // When
        updateUserUseCase.execute(userInput);

        // Then
        verify(userGateway).findById(uuid);
        verify(userGateway).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userGateway.findById(uuid)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> updateUserUseCase.execute(userInput))
                .isInstanceOf(UserNotFoundException.class);

        verify(userGateway).findById(uuid);
        verify(userGateway, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao passar UserInput nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        // When / Then
        assertThatThrownBy(() -> updateUserUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserInput cannot be null.");

        verifyNoInteractions(userGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção ao passar input nulo")
    void shouldThrowExceptionWhenUuidIsNull() {
        assertThatThrownBy(() -> updateUserUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserInput cannot be null");

        verifyNoInteractions(userGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção ao construir com gateway nulo")
    void shouldThrowExceptionWhenGatewayIsNull() {
        assertThatThrownBy(() -> new UpdateUserUseCase(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserGateway cannot be null.");
    }
}
