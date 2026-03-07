package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserEntity;

public class UserMapper {

    private UserMapper() {}

    public static UserEntity toEntity(User domain) {
        if (domain == null) return null;
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(domain.getUuid());
        userEntity.setRoles(domain.getRoles());
        return userEntity;
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(entity.getUuid(), entity.getRoles());
    }
}
