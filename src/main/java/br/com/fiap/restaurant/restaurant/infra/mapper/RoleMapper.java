package br.com.fiap.restaurant.restaurant.infra.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.model.Role;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleEntity toEntity(Role domain);
    Role toDomain(RoleEntity entity);
}
