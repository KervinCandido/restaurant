package br.com.fiap.restaurant.restaurant.core.usecase.user;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateUserUseCase")
class CreateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

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
        when(userGateway.findById(uuid)).thenReturn(Optional.empty());
        User savedUser = new User(uuid, roles);
        when(userGateway.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = createUserUseCase.execute(userInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(uuid);
        assertThat(result.getRoles()).isEqualTo(roles);

        verify(userGateway).findById(uuid);
        verify(userGateway).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário já existir")
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        when(userGateway.findById(uuid)).thenReturn(Optional.of(new User(uuid, roles)));

        // When / Then
        assertThatThrownBy(() -> createUserUseCase.execute(userInput))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User already exists.");

        verify(userGateway).findById(uuid);
        verify(userGateway, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao passar UserInput nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        // When / Then
        assertThatThrownBy(() -> createUserUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserInput cannot be null.");

        verifyNoInteractions(userGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção ao construir com gateway nulo")
    void shouldThrowExceptionWhenGatewayIsNull() {
        assertThatThrownBy(() -> new CreateUserUseCase(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UserGateway cannot be null.");
    }
}
