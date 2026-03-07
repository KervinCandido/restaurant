package br.com.fiap.restaurant.restaurant.core.presenter;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para RestaurantPresenter")
class RestaurantPresenterTest {

    @DisplayName("Deve converter Restaurant para RestaurantManagementOutput com sucesso")
    @Test
    void deveConverterRestaurantParaRestaurantManagementOutputComSucesso() {
        Long id = 1L;
        String name = "Restaurante Teste";
        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        String cuisineType = "Italiana";
        User owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));

        Restaurant restaurant = new Restaurant(id, name, address, cuisineType, owner);

        OpeningHours openingHours = new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0));
        restaurant.addOpeningHours(openingHours);

        MenuItem menuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        restaurant.addMenuItem(menuItem);

        User employee = new User(UUID.randomUUID(), Set.of("ROLE_USER"));
        restaurant.addEmployee(employee);

        RestaurantManagementOutput output = RestaurantPresenter.toManagementOutput(restaurant);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(name);
        assertThat(output.cuisineType()).isEqualTo(cuisineType);
        assertThat(output.address()).isNotNull();
        assertThat(output.owner()).isEqualTo(owner.getUuid());
        assertThat(output.openingHours()).hasSize(1);
        assertThat(output.menu()).hasSize(1);
        assertThat(output.employees()).hasSize(1).contains(employee.getUuid());
    }
}
