package br.com.fiap.restaurant.restaurant.core.domain;

import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.UserCannotBeRestaurantOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para Restaurant")
class RestaurantTest {

    private User owner;
    private Address address;

    @BeforeEach
    void setUp() {
        this.owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        this.address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
    }

    @DisplayName("Deve criar restaurante com sucesso")
    @Test
    void deveCriarRestauranteComSucesso() {
        Long id = 1L;
        String name = "Restaurante Teste";
        String cuisineType = "Italiana";

        Restaurant restaurant = new Restaurant(id, name, address, cuisineType, owner);

        assertThat(restaurant).isNotNull();
        assertThat(restaurant.getId()).isEqualTo(id);
        assertThat(restaurant.getName()).isEqualTo(name);
        assertThat(restaurant.getAddress()).isEqualTo(address);
        assertThat(restaurant.getCuisineType()).isEqualTo(cuisineType);
        assertThat(restaurant.getOwner()).isEqualTo(owner);
        assertThat(restaurant.getOpeningHours()).isEmpty();
        assertThat(restaurant.getMenuItems()).isEmpty();
        assertThat(restaurant.getEmployees()).isEmpty();
    }

    @DisplayName("Deve lançar NullPointerException se name for nulo")
    @Test
    void deveLancarNullPointerSeNameForNulo() {
        assertThatThrownBy(() -> new Restaurant(1L, null, address, "Italiana", owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O nome do restaurante não pode ser nulo");
    }

    @DisplayName("Deve lançar NullPointerException se address for nulo")
    @Test
    void deveLancarNullPointerSeAddressForNulo() {
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", null, "Italiana", owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O endereço não pode ser nulo");
    }

    @DisplayName("Deve lançar NullPointerException se cuisineType for nulo")
    @Test
    void deveLancarNullPointerSeCuisineTypeForNulo() {
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", address, null, owner))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O tipo de cozinha não pode ser nulo");
    }

    @DisplayName("Deve lançar NullPointerException se owner for nulo")
    @Test
    void deveLancarNullPointerSeOwnerForNulo() {
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", address, "Italiana", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("O dono do restaurante não pode ser nulo");
    }

    @DisplayName("Deve lançar BusinessException se name for vazio")
    @Test
    void deveLancarBusinessExceptionSeNameForVazio() {
        assertThatThrownBy(() -> new Restaurant(1L, "   ", address, "Italiana", owner))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O nome do restaurante não pode ser vazio");
    }

    @DisplayName("Deve lançar BusinessException se cuisineType for vazio")
    @Test
    void deveLancarBusinessExceptionSeCuisineTypeForVazio() {
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", address, "   ", owner))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("O tipo de cozinha não pode ser vazio");
    }

    @DisplayName("Deve lançar UserCannotBeRestaurantOwnerException se owner não tiver permissão")
    @Test
    void deveLancarUserCannotBeRestaurantOwnerExceptionSeOwnerNaoTiverPermissao() {
        User userNotOwner = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT));
        assertThatThrownBy(() -> new Restaurant(1L, "Restaurante Teste", address, "Italiana", userNotOwner))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class);
    }

    @DisplayName("Deve adicionar OpeningHours com sucesso")
    @Test
    void deveAdicionarOpeningHoursComSucesso() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        OpeningHours openingHours = new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0));
        restaurant.addOpeningHours(openingHours);

        assertThat(restaurant.getOpeningHours()).hasSize(1).contains(openingHours);
    }

    @DisplayName("Deve adicionar Coleção OpeningHours com sucesso")
    @Test
    void deveAdicionarColecaoDeOpeningHoursComSucesso() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        OpeningHours openingHours = new OpeningHours(1L, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0));
        restaurant.addOpeningHours(Set.of(openingHours));

        assertThat(restaurant.getOpeningHours()).hasSize(1).contains(openingHours);
    }

    @DisplayName("Deve adicionar MenuItem com sucesso")
    @Test
    void deveAdicionarMenuItemComSucesso() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        MenuItem menuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        restaurant.addMenuItem(menuItem);

        assertThat(restaurant.getMenuItems()).hasSize(1).contains(menuItem);
    }

    @DisplayName("Deve adicionar coleção de MenuItem com sucesso")
    @Test
    void deveAdicionarColecaoDeMenuItemComSucesso() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        MenuItem menuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");
        restaurant.addMenuItems(Set.of(menuItem));

        assertThat(restaurant.getMenuItems()).hasSize(1).contains(menuItem);
    }

    @DisplayName("Deve adicionar Employee com sucesso")
    @Test
    void deveAdicionarEmployeeComSucesso() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        User employee = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT));
        restaurant.addEmployee(employee);

        assertThat(restaurant.getEmployees()).hasSize(1).contains(employee);
    }

    @DisplayName("Deve adicionar coleção de Employees com sucesso")
    @Test
    void deveAdicionarColecaoDeEmployeeComSucesso() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        User employee = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT));
        restaurant.addEmployees(Set.of(employee));

        assertThat(restaurant.getEmployees()).hasSize(1).contains(employee);
    }

    @DisplayName("Deve verificar se pode ser gerenciado pelo dono")
    @Test
    void deveVerificarSePodeSerGerenciadoPeloDono() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        assertThat(restaurant.canBeManagedBy(owner)).isTrue();
    }

    @DisplayName("Deve verificar se pode ser gerenciado por funcionário")
    @Test
    void deveVerificarSePodeSerGerenciadoPorFuncionario() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        User employee = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT));
        restaurant.addEmployee(employee);

        assertThat(restaurant.canBeManagedBy(employee)).isTrue();
    }

    @DisplayName("Deve verificar que não pode ser gerenciado por usuário aleatório")
    @Test
    void deveVerificarQueNaoPodeSerGerenciadoPorUsuarioAleatorio() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        User randomUser = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT));

        assertThat(restaurant.canBeManagedBy(randomUser)).isFalse();
    }
    
    @DisplayName("Deve verificar que não pode ser gerenciado por usuário nulo")
    @Test
    void deveVerificarQueNaoPodeSerGerenciadoPorUsuarioNulo() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        assertThat(restaurant.canBeManagedBy(null)).isFalse();
    }

    @DisplayName("Deve retornar menu imutavel quando chamar o getMenu")
    @Test
    void deveRetornaMenuImutavelQuandoChamarGetMenu() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante Teste", address, "Italiana", owner);

        Set<MenuItem> menuItems = restaurant.getMenuItems();

        assertThat(menuItems).isEmpty();
        MenuItem menuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("50.00"), false, "/images/pizza.jpg");

        assertThatThrownBy(() -> menuItems.add(menuItem)).isInstanceOf(UnsupportedOperationException.class);
    }

    @DisplayName("Deve considerar iguais quando é a mesma instancia de restaurante")
    @Test
    void deveConsiderarIgualQuandoMesmaInstanciaRestaurantes() {
        Long id = 1L;
        Restaurant firstRestaurant = new Restaurant(id, "Restaurante 1", address, "Italiana", owner);
        Object secondRestaurant;
        secondRestaurant = firstRestaurant;
        assertThat(firstRestaurant).isEqualTo(secondRestaurant).hasSameHashCodeAs(secondRestaurant);
    }

    @DisplayName("Deve considerar iguais restaurantes com mesmo id")
    @Test
    void deveConsiderarIguaisRestaurantesComMesmoId() {
        Long id = 1L;
        Restaurant firstRestaurant = new Restaurant(id, "Restaurante 1", address, "Italiana", owner);
        Restaurant secondRestaurant = new Restaurant(id, "Restaurante 2", address, "Japonesa", owner);

        assertThat(firstRestaurant).isEqualTo(secondRestaurant).hasSameHashCodeAs(secondRestaurant);
    }

    @DisplayName("Deve considerar diferentes restaurantes com ids diferentes")
    @Test
    void deveConsiderarDiferentesRestaurantesComIdsDiferentes() {
        Restaurant firstRestaurant = new Restaurant(1L, "Restaurante 1", address, "Italiana", owner);
        Restaurant secondRestaurant = new Restaurant(2L, "Restaurante 1", address, "Italiana", owner);

        assertThat(firstRestaurant).isNotEqualTo(secondRestaurant).doesNotHaveSameHashCodeAs(secondRestaurant);
    }

    @DisplayName("Deve considerar diferentes restaurantes sem ids")
    @Test
    void deveConsiderarDiferentesRestaurantesSemIds() {
        Restaurant firstRestaurant = new Restaurant(1L, "Restaurante 1", address, "Italiana", owner);
        Restaurant secondRestaurant = new Restaurant(null, "Restaurante 1", address, "Italiana", owner);

        assertThat(firstRestaurant).isNotEqualTo(secondRestaurant).doesNotHaveSameHashCodeAs(secondRestaurant);
        assertThat(secondRestaurant).isNotEqualTo(firstRestaurant).doesNotHaveSameHashCodeAs(firstRestaurant);
    }


    @DisplayName("Deve considerar diferente se não instancia de Restaurant")
    @Test
    void deveConsiderarDiferentesSeForInstaciaDeOutroTipo() {
        Restaurant restaurant = new Restaurant(1L, "Restaurante 1", address, "Italiana", owner);
        Object otherObject = new Object();

        assertThat(restaurant).isNotEqualTo(otherObject).doesNotHaveSameHashCodeAs(otherObject);
    }
}
