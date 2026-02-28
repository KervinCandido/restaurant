package br.com.fiap.restaurant.restaurant.infra.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.model.User;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserTypeMapper.class, AddressMapper.class})
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "authorities", ignore = true)
    UserEntity toEntity(User domain);

    @Mapping(target = "passwordHash", source = "passwordHash")
    User toDomain(UserEntity entity);
}
