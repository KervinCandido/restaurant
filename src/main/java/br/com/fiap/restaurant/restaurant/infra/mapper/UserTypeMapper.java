package br.com.fiap.restaurant.restaurant.infra.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.model.UserType;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.UserTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserTypeMapper {
    UserTypeEntity toEntity(UserType domain);
    UserType toDomain(UserTypeEntity entity);
}
