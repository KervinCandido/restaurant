package br.com.fiap.restaurant.restaurant.core.usecase.user;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotFoundException;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteUserUseCase")
class DeleteUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    private UUID uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        // Given
        User existingUser = new User(uuid, Set.of("ROLE_USER"));
        when(userGateway.findById(uuid)).thenReturn(Optional.of(existingUser));

        // When
        deleteUserUseCase.execute(uuid);

        // Then
        verify(userGateway).findById(uuid);
        verify(userGateway).delete(uuid);
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir ao deletar")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userGateway.findById(uuid)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> deleteUserUseCase.execute(uuid))
                .isInstanceOf(UserNotFoundException.class);

        verify(userGateway).findById(uuid);
        verify(userGateway, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao passar UUID nulo")
    void shouldThrowExceptionWhenUuidIsNull() {
        // When / Then
        assertThatThrownBy(() -> deleteUserUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UUID cannot be null.");

        verifyNoInteractions(userGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção ao construir com gateway nulo")
    void shouldThrowExceptionWhenGatewayIsNull() {
        assertThatThrownBy(() -> new DeleteUserUseCase(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserGateway cannot be null.");
    }
}
