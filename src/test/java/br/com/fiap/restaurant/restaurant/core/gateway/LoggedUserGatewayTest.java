package br.com.fiap.restaurant.restaurant.core.gateway;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para LoggedUserGateway")
class LoggedUserGatewayTest {

    @Spy
    private LoggedUserGateway loggedUserGateway;

    @DisplayName("Deve retornar usuário atual quando autenticado")
    @Test
    void deveRetornarUsuarioAtualQuandoAutenticado() {
        User user = new User(UUID.randomUUID(), Set.of("ROLE_USER"));
        given(loggedUserGateway.getCurrentUser()).willReturn(Optional.of(user));

        User currentUser = loggedUserGateway.requireCurrentUser();

        assertThat(currentUser).isNotNull().isEqualTo(user);
    }

    @DisplayName("Deve lançar UserNotAuthenticatedException quando não autenticado")
    @Test
    void deveLancarUserNotAuthenticatedExceptionQuandoNaoAutenticado() {
        given(loggedUserGateway.getCurrentUser()).willReturn(Optional.empty());

        assertThatThrownBy(() -> loggedUserGateway.requireCurrentUser())
                .isInstanceOf(UserNotAuthenticatedException.class);
    }
}
