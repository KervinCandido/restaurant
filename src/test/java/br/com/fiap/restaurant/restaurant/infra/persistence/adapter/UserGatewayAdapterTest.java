package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import(UserGatewayAdapter.class)
@DisplayName("Testes de Integração para UserGatewayAdapter")
class UserGatewayAdapterTest {

    @Autowired
    private UserGatewayAdapter adapter;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve encontrar usuário por ID")
    void deveEncontrarUsuarioPorId() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setRoles(Set.of("CUSTOMER"));
        entity = userRepository.save(entity);

        // When
        Optional<User> result = adapter.findById(entity.getUuid());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo(entity.getUuid());
        assertThat(result.get().getRoles()).contains("CUSTOMER");
    }

    @Test
    @DisplayName("Deve retornar vazio quando usuário não existe")
    void deveRetornarVazioQuandoUsuarioNaoExiste() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<User> result = adapter.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar todos os usuários por IDs")
    void deveEncontrarTodosUsuariosPorIds() {
        // Given
        UserEntity entity1 = new UserEntity();
        entity1.setUuid(UUID.randomUUID());
        entity1.setRoles(Set.of("CUSTOMER"));
        userRepository.save(entity1);

        UserEntity entity2 = new UserEntity();
        entity2.setUuid(UUID.randomUUID());
        entity2.setRoles(Set.of("ADMIN"));
        entity2 = userRepository.save(entity2);

        List<UUID> ids = List.of(entity1.getUuid(), entity2.getUuid());

        // When
        List<User> result = adapter.findAllById(ids);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUuid).containsExactlyInAnyOrder(entity1.getUuid(), entity2.getUuid());
    }

    @Test
    @DisplayName("Deve retornar vazio se os ids forem vazios")
    void deveRetornarVazioSeOsIdsForemVazios() {
        // Given
        List<UUID> ids = List.of();

        // When
        List<User> result = adapter.findAllById(ids);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve salvar um usuário")
    void deveSalvarUmUsuario() {
        // Given
        User user = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));


        // When
        User savedUser = adapter.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUuid()).isEqualTo(user.getUuid());
        Optional<UserEntity> found = userRepository.findById(user.getUuid());
        assertThat(found).isPresent();
        assertThat(found.get().getUuid()).isEqualTo(user.getUuid());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar usuário nulo")
    void deveLancarExcecaoAoSalvarUsuarioNulo() {
        // Then
        assertThatThrownBy(() -> adapter.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("user cannot be null");
    }

    @Test
    @DisplayName("Deve deletar um usuário")
    void deveDeletarUmUsuario() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setRoles(Set.of(User.RESTAURANT_OWNER));
        userRepository.save(entity);

        // When
        adapter.delete(entity.getUuid());

        // Then
        Optional<UserEntity> found = userRepository.findById(entity.getUuid());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário com ID nulo")
    void deveLancarExcecaoAoDeletarUsuarioComIdNulo() {
        // Then
        assertThatThrownBy(() -> adapter.delete(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("userUuid cannot be null");
    }
}
