package br.com.fiap.restaurant.restaurant.infra.persistence.mapper;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.*;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantMapperTest {

    @Test
    void shouldReturnNullWhenRestaurantDomainIsNull() {
        RestaurantEntity restaurantEntity = RestaurantMapper.toEntity(null);
        assertThat(restaurantEntity).isNull();
    }

    @Test
    void shouldConvertRestaurantDomainToRestaurantEntity() {
        User owner = new User(UUID.randomUUID(), Collections.singleton(User.RESTAURANT_OWNER));
        Address address = new Address("street", "number", "city", "state", "zipCode", "complement");
        Restaurant restaurant = new Restaurant(1L, "name", address, "cuisineType", owner);
        restaurant.addOpeningHours(new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)));
        restaurant.addEmployee(new User(UUID.randomUUID(), Collections.singleton(User.RESTAURANT_OWNER)));

        RestaurantEntity restaurantEntity = RestaurantMapper.toEntity(restaurant);

        assertThat(restaurantEntity).isNotNull();
        assertThat(restaurantEntity.getId()).isEqualTo(restaurant.getId());
        assertThat(restaurantEntity.getName()).isEqualTo(restaurant.getName());
        assertThat(restaurantEntity.getCuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(restaurantEntity.getOwner().getUuid()).isEqualTo(owner.getUuid());
        assertThat(restaurantEntity.getAddress().getStreet()).isEqualTo(address.getStreet());
        assertThat(restaurantEntity.getOpeningHours()).hasSize(1);
        assertThat(restaurantEntity.getEmployees()).hasSize(1);
    }

    @Test
    void shouldReturnNullWhenRestaurantEntityIsNull() {
        Restaurant restaurant = RestaurantMapper.toDomain(null);
        assertThat(restaurant).isNull();
    }

    @Test
    void shouldConvertRestaurantEntityToRestaurantDomain() {
        UserEntity ownerEntity = new UserEntity();
        ownerEntity.setUuid(UUID.randomUUID());
        ownerEntity.setRoles(Collections.singleton(User.RESTAURANT_OWNER));

        UserEntity employee = new UserEntity();
        employee.setUuid(UUID.randomUUID());
        employee.setRoles(Collections.singleton(Restaurant.VIEW_RESTAURANT));

        AddressEmbeddableEntity addressEntity = new AddressEmbeddableEntity();
        addressEntity.setStreet("street");

        OpeningHoursEntity openingHoursEntity = new OpeningHoursEntity();
        openingHoursEntity.setDayOfWeek(DayOfWeek.MONDAY);
        openingHoursEntity.setOpenHour(LocalTime.of(18, 0));
        openingHoursEntity.setCloseHour(LocalTime.of(22, 0));

        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(1L);
        restaurantEntity.setName("name");
        restaurantEntity.setCuisineType("cuisineType");
        restaurantEntity.setOwner(ownerEntity);
        restaurantEntity.setAddress(addressEntity);
        restaurantEntity.setOpeningHours(Collections.singleton(openingHoursEntity));

        restaurantEntity.setEmployees(Collections.singleton(new RestaurantEmployeeEntity(restaurantEntity, employee)));

        Restaurant restaurant = RestaurantMapper.toDomain(restaurantEntity);

        assertThat(restaurant).isNotNull();
        assertThat(restaurant.getId()).isEqualTo(restaurantEntity.getId());
        assertThat(restaurant.getName()).isEqualTo(restaurantEntity.getName());
        assertThat(restaurant.getCuisineType()).isEqualTo(restaurantEntity.getCuisineType());
        assertThat(restaurant.getOwner().getUuid()).isEqualTo(ownerEntity.getUuid());
        assertThat(restaurant.getAddress().getStreet()).isEqualTo(addressEntity.getStreet());
        assertThat(restaurant.getOpeningHours()).hasSize(1);
        assertThat(restaurant.getEmployees()).hasSize(1);
    }
}
