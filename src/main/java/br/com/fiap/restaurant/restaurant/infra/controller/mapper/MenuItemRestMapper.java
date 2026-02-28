package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.inbound.CreateMenuItemInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateMenuItemInput;
import br.com.fiap.restaurant.restaurant.core.outbound.MenuItemOutput;
import br.com.fiap.restaurant.restaurant.infra.controller.request.MenuItemRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.MenuItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemRestMapper {
    MenuItemResponse toResponse(MenuItemOutput output);

    @Mapping(target = "restaurantId", expression = "java(restaurantId)")
    CreateMenuItemInput toCreateInput(MenuItemRequest menuItemRequest, Long restaurantId);

    @Mapping(target = "id", expression = "java(id)")
    UpdateMenuItemInput toUpdateInput(MenuItemRequest menuItemRequest, Long id);
}
