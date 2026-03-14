package br.com.fiap.restaurant.restaurant.core.outbound;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para UserOutput")
class UserOutputTest {

    @Test
    @DisplayName("Deve criar UserOutput com sucesso")
    void shouldCreateUserOutput() {
        // Given
        UUID uuid = UUID.randomUUID();
        Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");

        // When
        UserOutput userOutput = new UserOutput(uuid, roles);

        // Then
        assertThat(userOutput.uuid()).isEqualTo(uuid);
        assertThat(userOutput.roles()).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}
