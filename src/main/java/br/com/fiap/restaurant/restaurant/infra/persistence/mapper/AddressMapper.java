package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.AddressEmbeddableEntity;

public class AddressMapper {

    private AddressMapper() {}

    public static AddressEmbeddableEntity toEntity(Address domain) {
        if (domain == null) return null;

        AddressEmbeddableEntity addressEmbeddableEntity = new AddressEmbeddableEntity();
        addressEmbeddableEntity.setStreet(domain.getStreet());
        addressEmbeddableEntity.setNumber(domain.getNumber());
        addressEmbeddableEntity.setCity(domain.getCity());
        addressEmbeddableEntity.setState(domain.getState());
        addressEmbeddableEntity.setZipCode(domain.getZipCode());
        addressEmbeddableEntity.setComplement(domain.getComplement());

        return addressEmbeddableEntity;
    }

    public static Address toDomain(AddressEmbeddableEntity entity) {
        if (entity == null) return null;

        return new Address (
            entity.getStreet(),
            entity.getNumber(),
            entity.getCity(),
            entity.getState(),
            entity.getZipCode(),
            entity.getComplement()
        );
    }
}
