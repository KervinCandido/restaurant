package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.inbound.AddressInput;
import br.com.fiap.restaurant.restaurant.infra.controller.response.AddressResponse;
import br.com.fiap.restaurant.restaurant.infra.controller.resquest.AddressRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressRestMapper {
    AddressInput toInput(AddressRequest addressRequest);
    AddressResponse toResponse(AddressInput addressInput);
}
