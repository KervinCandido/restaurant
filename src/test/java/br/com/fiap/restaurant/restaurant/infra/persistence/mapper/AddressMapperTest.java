package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.AddressEmbeddableEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressMapperTest {

    @Test
    void shouldReturnNullWhenAddressDomainIsNull() {
        AddressEmbeddableEntity addressEmbeddableEntity = AddressMapper.toEntity(null);
        assertThat(addressEmbeddableEntity).isNull();
    }

    @Test
    void shouldConvertAddressDomainToAddressEntity() {
        Address address = new Address("street", "number", "city", "state", "zipCode", "complement");

        AddressEmbeddableEntity addressEmbeddableEntity = AddressMapper.toEntity(address);

        assertThat(addressEmbeddableEntity).isNotNull();
        assertThat(addressEmbeddableEntity.getStreet()).isEqualTo(address.getStreet());
        assertThat(addressEmbeddableEntity.getNumber()).isEqualTo(address.getNumber());
        assertThat(addressEmbeddableEntity.getCity()).isEqualTo(address.getCity());
        assertThat(addressEmbeddableEntity.getState()).isEqualTo(address.getState());
        assertThat(addressEmbeddableEntity.getZipCode()).isEqualTo(address.getZipCode());
        assertThat(addressEmbeddableEntity.getComplement()).isEqualTo(address.getComplement());
    }

    @Test
    void shouldReturnNullWhenAddressEntityIsNull() {
        Address address = AddressMapper.toDomain(null);
        assertThat(address).isNull();
    }

    @Test
    void shouldConvertAddressEntityToAddressDomain() {
        AddressEmbeddableEntity addressEmbeddableEntity = new AddressEmbeddableEntity();
        addressEmbeddableEntity.setStreet("street");
        addressEmbeddableEntity.setNumber("number");
        addressEmbeddableEntity.setCity("city");
        addressEmbeddableEntity.setState("state");
        addressEmbeddableEntity.setZipCode("zipCode");
        addressEmbeddableEntity.setComplement("complement");

        Address address = AddressMapper.toDomain(addressEmbeddableEntity);

        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo(addressEmbeddableEntity.getStreet());
        assertThat(address.getNumber()).isEqualTo(addressEmbeddableEntity.getNumber());
        assertThat(address.getCity()).isEqualTo(addressEmbeddableEntity.getCity());
        assertThat(address.getState()).isEqualTo(addressEmbeddableEntity.getState());
        assertThat(address.getZipCode()).isEqualTo(addressEmbeddableEntity.getZipCode());
        assertThat(address.getComplement()).isEqualTo(addressEmbeddableEntity.getComplement());
    }
}
