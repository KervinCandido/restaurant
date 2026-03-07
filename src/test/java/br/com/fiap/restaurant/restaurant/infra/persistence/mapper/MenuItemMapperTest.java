package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MenuItemMapperTest {

    @Test
    void shouldReturnNullWhenMenuItemDomainAndRestaurantEntityAreNull() {
        MenuItemEntity menuItemEntity = MenuItemMapper.toEntity(null, null);
        assertThat(menuItemEntity).isNull();
    }

    @Test
    void shouldConvertMenuItemDomainToMenuItemEntity() {
        MenuItem menuItem = new MenuItem(1L, "name", "description", BigDecimal.TEN, true, "photoPath");
        RestaurantEntity restaurantEntity = new RestaurantEntity();

        MenuItemEntity menuItemEntity = MenuItemMapper.toEntity(menuItem, restaurantEntity);

        assertThat(menuItemEntity).isNotNull();
        assertThat(menuItemEntity.getId()).isEqualTo(menuItem.getId());
        assertThat(menuItemEntity.getName()).isEqualTo(menuItem.getName());
        assertThat(menuItemEntity.getDescription()).isEqualTo(menuItem.getDescription());
        assertThat(menuItemEntity.getPrice()).isEqualTo(menuItem.getPrice());
        assertThat(menuItemEntity.getRestaurantOnly()).isEqualTo(menuItem.getRestaurantOnly());
        assertThat(menuItemEntity.getPhotoPath()).isEqualTo(menuItem.getPhotoPath());
        assertThat(menuItemEntity.getRestaurant()).isEqualTo(restaurantEntity);
    }

    @Test
    void shouldReturnNullWhenMenuItemEntityIsNull() {
        MenuItem menuItem = MenuItemMapper.toDomain(null);
        assertThat(menuItem).isNull();
    }

    @Test
    void shouldConvertMenuItemEntityToMenuItemDomain() {
        MenuItemEntity menuItemEntity = new MenuItemEntity();
        menuItemEntity.setId(1L);
        menuItemEntity.setName("name");
        menuItemEntity.setDescription("description");
        menuItemEntity.setPrice(BigDecimal.TEN);
        menuItemEntity.setRestaurantOnly(true);
        menuItemEntity.setPhotoPath("photoPath");

        MenuItem menuItem = MenuItemMapper.toDomain(menuItemEntity);

        assertThat(menuItem).isNotNull();
        assertThat(menuItem.getId()).isEqualTo(menuItemEntity.getId());
        assertThat(menuItem.getName()).isEqualTo(menuItemEntity.getName());
        assertThat(menuItem.getDescription()).isEqualTo(menuItemEntity.getDescription());
        assertThat(menuItem.getPrice()).isEqualTo(menuItemEntity.getPrice());
        assertThat(menuItem.getRestaurantOnly()).isEqualTo(menuItemEntity.getRestaurantOnly());
        assertThat(menuItem.getPhotoPath()).isEqualTo(menuItemEntity.getPhotoPath());
    }
}
