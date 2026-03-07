package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void shouldReturnNullWhenUserDomainIsNull() {
        UserEntity userEntity = UserMapper.toEntity(null);
        assertThat(userEntity).isNull();
    }

    @Test
    void shouldConvertUserDomainToUserEntity() {
        User user = new User(UUID.randomUUID(), Set.of("ROLE_USER"));

        UserEntity userEntity = UserMapper.toEntity(user);

        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getUuid()).isEqualTo(user.getUuid());
        assertThat(userEntity.getRoles()).isEqualTo(user.getRoles());
    }

    @Test
    void shouldReturnNullWhenUserEntityIsNull() {
        User user = UserMapper.toDomain(null);
        assertThat(user).isNull();
    }

    @Test
    void shouldConvertUserEntityToUserDomain() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID());
        userEntity.setRoles(Set.of("ROLE_USER"));

        User user = UserMapper.toDomain(userEntity);

        assertThat(user).isNotNull();
        assertThat(user.getUuid()).isEqualTo(userEntity.getUuid());
        assertThat(user.getRoles()).isEqualTo(userEntity.getRoles());
    }
}
