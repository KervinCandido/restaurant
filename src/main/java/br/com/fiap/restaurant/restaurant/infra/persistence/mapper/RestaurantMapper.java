package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.*;

import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantMapper {
    private RestaurantMapper() {}

    public static RestaurantEntity toEntity(Restaurant domain) {
        if (domain == null) return null;

        RestaurantEntity restaurantEntity = new RestaurantEntity();

        UserEntity owner = UserMapper.toEntity(domain.getOwner());

        AddressEmbeddableEntity address = AddressMapper.toEntity(domain.getAddress());

        Set<OpeningHoursEntity> openingHours = toOpeningHoursEntity(domain, restaurantEntity);

        Set<MenuItemEntity> menu = toMenuItemEntity(domain, restaurantEntity);

        Set<RestaurantEmployeeEntity> employees = toEmployeesEntity(domain, restaurantEntity);

        restaurantEntity.setOwner(owner);
        restaurantEntity.setId(domain.getId());
        restaurantEntity.setName(domain.getName());
        restaurantEntity.setAddress(address);
        restaurantEntity.setCuisineType(domain.getCuisineType());
        restaurantEntity.setOpeningHours(openingHours);
        restaurantEntity.setMenu(menu);
        restaurantEntity.setEmployees(employees);

        return restaurantEntity;
    }

    private static Set<RestaurantEmployeeEntity> toEmployeesEntity(Restaurant domain, RestaurantEntity restaurantEntity) {
        return domain.getEmployees()
                .stream()
                .map(ree -> new RestaurantEmployeeEntity(restaurantEntity, UserMapper.toEntity(ree)))
                .collect(Collectors.toSet());
    }

    private static Set<MenuItemEntity> toMenuItemEntity(Restaurant domain, RestaurantEntity restaurantEntity) {
        return domain.getMenuItems()
                .stream().map(m -> MenuItemMapper.toEntity(m, restaurantEntity)).collect(Collectors.toSet());
    }

    private static Set<OpeningHoursEntity> toOpeningHoursEntity(Restaurant domain, RestaurantEntity restaurantEntity) {
        return domain.getOpeningHours()
                .stream().map(o -> OpeningHoursMapper.toEntity(o, restaurantEntity)).collect(Collectors.toSet());
    }

    public static Restaurant toDomain(RestaurantEntity entity) {
        if (entity == null) return null;

        User owner = UserMapper.toDomain(entity.getOwner());

        Restaurant restaurant = new Restaurant (
            entity.getId(),
            entity.getName(),
            AddressMapper.toDomain(entity.getAddress()),
            entity.getCuisineType(),
            owner
        );

        if (entity.getOpeningHours() != null) {
            entity.getOpeningHours()
                    .parallelStream()
                    .map(OpeningHoursMapper::toDomain)
                    .forEach(restaurant::addOpeningHours);
        }

        if (entity.getMenu() != null) {
            entity.getMenu()
                    .parallelStream()
                    .map(MenuItemMapper::toDomain)
                    .forEach(restaurant::addMenuItem);
        }

        if (entity.getEmployees() != null) {
            entity.getEmployees()
                    .parallelStream()
                    .map(RestaurantEmployeeEntity::getEmployee)
                    .map(UserMapper::toDomain)
                    .forEach(restaurant::addEmployee);
        }

        return restaurant;
    }
}
