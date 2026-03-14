package br.com.fiap.restaurant.restaurant.core.inbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserInput")
class UserInputTest {

    @Test
    @DisplayName("Deve criar UserInput com sucesso")
    void shouldCreateUserInput() {
        // Given
        UUID uuid = UUID.randomUUID();
        Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");

        // When
        UserInput userInput = new UserInput(uuid, roles);

        // Then
        assertThat(userInput.uuid()).isEqualTo(uuid);
        assertThat(userInput.roles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}
