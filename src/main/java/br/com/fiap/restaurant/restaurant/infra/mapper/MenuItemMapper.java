package br.com.fiap.restaurant.restaurant.infra.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.model.MenuItem;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {

    @Mapping(target = "restaurant", ignore = true)
    MenuItemEntity toEntity(MenuItem domain);

    @Mapping(target = "restaurant", source = "restaurantEntity")
    @Mapping(target = "id", source = "domain.id")
    @Mapping(target = "name", source = "domain.name")
    MenuItemEntity toEntity(MenuItem domain, RestaurantEntity restaurantEntity);

    MenuItem toDomain(MenuItemEntity entity);
}
