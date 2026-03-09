package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;

public class MenuItemMapper {

    private MenuItemMapper() {}

    public static MenuItemEntity toEntity(MenuItem domain, RestaurantEntity restaurantEntity) {
        if (domain == null && restaurantEntity == null) {
            return null;
        }
        MenuItemEntity menuItemEntity = new MenuItemEntity();
        
        if (domain != null) {
            menuItemEntity.setId(domain.getId());
            menuItemEntity.setName(domain.getName());
            menuItemEntity.setDescription(domain.getDescription());
            menuItemEntity.setPrice(domain.getPrice());
            menuItemEntity.setRestaurantOnly(domain.getRestaurantOnly());
            menuItemEntity.setPhotoPath(domain.getPhotoPath());
        }
        menuItemEntity.setRestaurant(restaurantEntity);
        return menuItemEntity;
    }

    public static MenuItem toDomain(MenuItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MenuItem (
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getRestaurantOnly(),
            entity.getPhotoPath()
        );
    }
}
