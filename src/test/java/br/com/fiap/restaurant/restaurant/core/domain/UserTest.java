package br.com.fiap.restaurant.restaurant.core.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para User")
class UserTest {

    @DisplayName("Deve criar user com sucesso")
    @Test
    void deveCriarUserComSucesso() {
        UUID userUuid = UUID.randomUUID();
        String roleUser = User.RESTAURANT_OWNER;
        User user = new User(userUuid, Set.of(roleUser));

        assertThat(user).isNotNull();
        assertThat(user.getUuid()).isNotNull().isEqualTo(userUuid);
        assertThat(user.getRoles()).isNotNull().hasSize(1).containsExactlyInAnyOrder(roleUser);
    }

    @DisplayName("Deve lançar nullpointer se o uuid for nulo")
    @Test
    void deveLancaNullPointerSeUuidForNulo() {
        Set<String> userRoles = Set.of("ROLE_USER");
        assertThatThrownBy(() -> new User(null, userRoles))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("uuid não pode ser nulo");
    }

    @DisplayName("Deve lançar nullpointer se o roles for nulo")
    @Test
    void deveLancaNullPointerSeRolesForNulo() {
        UUID uuid = UUID.randomUUID();
        assertThatThrownBy(() -> new User(uuid, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("roles não pode ser nulo");
    }

    @DisplayName("Deve considerar iguais users com mesmo uuid")
    @Test
    void deveConsiderarIguaisUsersComMesmoUuid() {
        UUID userUuid = UUID.randomUUID();
        String roleUser = User.RESTAURANT_OWNER;
        User firstUser = new User(userUuid, Set.of(roleUser));
        User secondUser = new User(userUuid, Set.of(roleUser));

        assertThat(firstUser).isEqualTo(secondUser).hasSameHashCodeAs(secondUser);
    }

    @DisplayName("Deve considerar diferente usuario com uuid diferente")
    @Test
    void deveConsiderarDiferentesUsersComMesmoUuid() {
        String roleUser = User.RESTAURANT_OWNER;
        User firstUser = new User(UUID.randomUUID(), Set.of(roleUser));
        User secondUser = new User(UUID.randomUUID(), Set.of(roleUser));

        assertThat(firstUser).isNotEqualTo(secondUser).doesNotHaveSameHashCodeAs(secondUser);
    }

    @DisplayName("Deve considerar diferente se não instancia de user")
    @Test
    void deveConsiderarDiferentesSeForInstaciaDeOutroTipo() {
        String roleUser = User.RESTAURANT_OWNER;
        User firstUser = new User(UUID.randomUUID(), Set.of(roleUser));
        Object second = new Object();
        assertThat(firstUser).isNotEqualTo(second).doesNotHaveSameHashCodeAs(second);
    }
}