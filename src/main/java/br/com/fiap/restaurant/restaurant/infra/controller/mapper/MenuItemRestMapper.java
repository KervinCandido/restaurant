package br.com.fiap.restaurant.restaurant.infra.controller.mapper;

import br.com.fiap.restaurant.restaurant.core.outbound.MenuItemOutput;
import br.com.fiap.restaurant.restaurant.infra.controller.response.MenuItemResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuItemRestMapper {
    MenuItemResponse toResponse(MenuItemOutput output);
}
