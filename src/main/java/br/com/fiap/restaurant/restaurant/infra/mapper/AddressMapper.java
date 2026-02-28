package br.com.fiap.restaurant.restaurant.infra.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.model.valueobject.Address;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.AddressEmbeddableEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressEmbeddableEntity toEntity(Address domain);
    Address toDomain(AddressEmbeddableEntity entity);
}
