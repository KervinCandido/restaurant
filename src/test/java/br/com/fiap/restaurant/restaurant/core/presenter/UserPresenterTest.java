package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.outbound.UserOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserPresenter")
class UserPresenterTest {

    @Test
    @DisplayName("Deve converter User para UserOutput com sucesso")
    void shouldConvertUserToUserOutput() {
        // Given
        UUID uuid = UUID.randomUUID();
        Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");
        User user = new User(uuid, roles);

        // When
        UserOutput result = UserPresenter.toOutput(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(uuid);
        assertThat(result.roles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Deve retornar null se User for null")
    void shouldReturnNullIfUserIsNull() {
        // When
        UserOutput result = UserPresenter.toOutput(null);

        // Then
        assertThat(result).isNull();
    }
}
