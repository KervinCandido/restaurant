package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantPublicOutput;
import br.com.fiap.restaurant.restaurant.infra.controller.request.RestaurantRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantResponse;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OpeningHoursRestMapper.class, AddressRestMapper.class, MenuItemRestMapper.class})
public interface RestaurantRestMapper {

    CreateRestaurantInput toInput(RestaurantRequest request);

    RestaurantResponse toResponse(RestaurantManagementOutput output);

    @Mapping(target = "id", expression = "java(id)")
    @Mapping(target = "owner", source = "request.ownerId")
    UpdateRestaurantInput toUpdateInput(RestaurantRequest request, Long id);

    @Mapping(target = "menu", source = "menuItems")
    RestaurantSummaryResponse toResponseSummary(RestaurantPublicOutput output);
}
