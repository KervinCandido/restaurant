package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.inbound.CreateUserTypeInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateUserTypeInput;
import br.com.fiap.restaurant.restaurant.core.outbound.UserTypeOutput;
import br.com.fiap.restaurant.restaurant.infra.controller.request.UserTypeRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.UserTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserTypeRestMapper {
    UserTypeResponse toResponse(UserTypeOutput output);
    CreateUserTypeInput toInput(UserTypeRequest response);
    @Mapping(target = "id", source = "id")
    UpdateUserTypeInput toUpdateInput(UserTypeRequest response, Long id);
}
