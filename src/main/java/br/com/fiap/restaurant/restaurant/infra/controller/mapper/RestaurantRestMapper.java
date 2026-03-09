package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import br.com.fiap.restaurant.restaurant.infra.controller.request.RestaurantRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {OpeningHoursRestMapper.class, AddressRestMapper.class, MenuItemRestMapper.class})
public interface RestaurantRestMapper {

    CreateRestaurantInput toInput(RestaurantRequest request);

    RestaurantResponse toResponse(RestaurantManagementOutput output);
}
