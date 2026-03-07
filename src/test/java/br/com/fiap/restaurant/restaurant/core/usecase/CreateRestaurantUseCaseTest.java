package br.com.fiap.restaurant.restaurant.core.usecase;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotFoundException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.NotifierGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.AddressInput;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.restaurant.core.inbound.OpeningHoursInput;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import br.com.fiap.restaurant.restaurant.utils.core.OpeningHoursBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateRestaurantUseCase")
class CreateRestaurantUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Mock
    private UserGateway userGateway;

    @Mock
    private NotifierGateway<Restaurant> createRestaurantNotifierGateway;

    private CreateRestaurantUseCase createRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> restaurantArgumentCaptor;

    private AddressInput addressInput;
    private Address address;
    private Set<OpeningHoursInput> openingHoursInput;
    private Set<OpeningHours> openingHours;
    private Set<MenuItemInput> itemsMenuInput;
    private Set<MenuItem> itemsMenu;
    private Set<UUID> employees;
    private User owner;


    @BeforeEach
    void setUp() {
        this.addressInput = new AddressInput("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        this.address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        this.owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));

        this.openingHoursInput = new HashSet<>(3);
        this.openingHoursInput.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.THURSDAY).buildInput());
        this.openingHoursInput.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.FRIDAY).buildInput());
        this.openingHoursInput.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.SATURDAY).buildInput());

        this.openingHours = new HashSet<>(3);
        this.openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.THURSDAY).build());
        this.openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.FRIDAY).build());
        this.openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.SATURDAY).build());

        this.itemsMenuInput = new HashSet<>(3);
        this.itemsMenuInput.add(MenuItemBuilder.builder().withoutId().buildInput());
        this.itemsMenuInput.add(MenuItemBuilder.builder().withoutId().buildInput());
        this.itemsMenuInput.add(MenuItemBuilder.builder().withoutId().buildInput());

        this.itemsMenu = new HashSet<>(3);
        this.itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        this.itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        this.itemsMenu.add(MenuItemBuilder.builder().withoutId().build());

        this.employees = new HashSet<>(1);
        this.employees.add(UUID.randomUUID());

        this.createRestaurantUseCase = new CreateRestaurantUseCase(loggedUserGateway, restaurantGateway, userGateway, List.of(createRestaurantNotifierGateway));
    }

    @DisplayName("Deve criar restaurante com sucesso")
    @Test
    void deveCriarRestauranteComSucesso() {
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";

        CreateRestaurantInput createRestaurantInput = new CreateRestaurantInput (
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                itemsMenuInput,
                employees,
                owner.getUuid());

        Restaurant restaurant = new Restaurant(1L, restaurantName, address, cuisineType, owner);
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItems(itemsMenu);
        restaurant.addEmployees(employees.stream().map(this::createUser).toList());

        given(loggedUserGateway.hasRole(Restaurant.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(owner.getUuid())).willReturn(Optional.of(owner));
        given(userGateway.findAllById(employees)).willReturn(restaurant.getEmployees().stream().toList());
        given(restaurantGateway.save(any(Restaurant.class))).willReturn(restaurant);
        given(createRestaurantNotifierGateway.send(restaurant)).willReturn(new CompletableFuture<>());

        Restaurant newRestaurant = createRestaurantUseCase.execute(createRestaurantInput);

        assertThat(newRestaurant).isNotNull();

        then(loggedUserGateway).should().hasRole(Restaurant.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(owner.getUuid());
        then(userGateway).should().findAllById(employees);
        then(restaurantGateway).should().save(restaurantArgumentCaptor.capture());
        then(createRestaurantNotifierGateway).should().send(restaurant);

        var restaurantCaptured = restaurantArgumentCaptor.getValue();

        assertThat(restaurantCaptured).isNotNull();
        assertThat(restaurantCaptured.getId()).isNull();
        assertThat(restaurantCaptured.getName()).isEqualTo(restaurantName);
        assertThat(restaurantCaptured.getAddress()).isNotNull().usingRecursiveComparison().isEqualTo(address);
        assertThat(restaurantCaptured.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(restaurantCaptured.getOwner()).isNotNull().isEqualTo(owner);
        assertThat(restaurantCaptured.getOpeningHours()).isNotNull().containsExactlyInAnyOrderElementsOf(openingHours);
        assertThat(restaurantCaptured.getMenuItems()).isNotNull().containsExactlyInAnyOrderElementsOf(itemsMenu);
        assertThat(restaurantCaptured.getEmployees()).isNotNull().containsExactlyInAnyOrderElementsOf(restaurant.getEmployees());
    }

    private User createUser(UUID uuid) {
        return new User(uuid, Set.of(Restaurant.VIEW_RESTAURANT));
    }

    @DisplayName("Deve lançar nullpointer se o input for nulo")
    @Test
    void deveLancarNullpointerSeInputForNulo() {
        assertThatThrownBy(()-> createRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("createRestaurantInput cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(createRestaurantNotifierGateway).shouldHaveNoInteractions();
    }

    @DisplayName("Deve lançar OperationNotAllowedException se o usuário não tiver permissão para criar restaurante")
    @Test
    void deveLancaOperationNotAllowedExceptionSeUsuarioNaoTiverPermissaoParaCriarRestaurante() {
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";

        CreateRestaurantInput createRestaurantInput = new CreateRestaurantInput (
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                itemsMenuInput,
                employees,
                owner.getUuid());

        given(loggedUserGateway.hasRole(Restaurant.CREATE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(()-> createRestaurantUseCase.execute(createRestaurantInput))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("current user does not have permission to perform this action");

        then(loggedUserGateway).should().hasRole(Restaurant.CREATE_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(createRestaurantNotifierGateway).shouldHaveNoInteractions();
    }

    @DisplayName("Deve lançar RestaurantNameIsAlreadyInUseException se o nome já existiver sendo usado")
    @Test
    void deveLancarRestaurantNameIsAlreadyInUseExceptionSeNomeJaExistirSendo() {
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";

        CreateRestaurantInput createRestaurantInput = new CreateRestaurantInput (
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                itemsMenuInput,
                employees,
                owner.getUuid());

        given(loggedUserGateway.hasRole(Restaurant.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(true);

        assertThatThrownBy(()-> createRestaurantUseCase.execute(createRestaurantInput))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class)
                .hasMessageContaining("Restaurant name is already in use");

        then(loggedUserGateway).should().hasRole(Restaurant.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoMoreInteractions();
        then(createRestaurantNotifierGateway).shouldHaveNoInteractions();
    }

    @DisplayName("Deve lançar UserNotFoundException quando o owner não é encontrado")
    @Test
    void deveLancarUserNotFoundExceptionQuandoOwnerNaoForEncontrado() {
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";

        CreateRestaurantInput createRestaurantInput = new CreateRestaurantInput (
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                itemsMenuInput,
                employees,
                owner.getUuid());

        given(loggedUserGateway.hasRole(Restaurant.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(owner.getUuid())).willReturn(Optional.empty());

        assertThatThrownBy(()-> createRestaurantUseCase.execute(createRestaurantInput))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User %s not found.", owner.getUuid().toString());

        then(loggedUserGateway).should().hasRole(Restaurant.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(owner.getUuid());
        then(userGateway).shouldHaveNoMoreInteractions();
        then(restaurantGateway).shouldHaveNoMoreInteractions();
        then(createRestaurantNotifierGateway).shouldHaveNoInteractions();
    }

    @DisplayName("Deve lançar UserNotFoundException se não encontrar algum employee")
    @Test
    void deveLancarUserNotFoundExceptionSeNaoEncontrarAlgumDosEmployees() {
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";

        CreateRestaurantInput createRestaurantInput = new CreateRestaurantInput (
                restaurantName,
                addressInput,
                cuisineType,
                openingHoursInput,
                itemsMenuInput,
                employees,
                owner.getUuid());

        given(loggedUserGateway.hasRole(Restaurant.CREATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.existsRestaurantWithName(restaurantName)).willReturn(false);
        given(userGateway.findById(owner.getUuid())).willReturn(Optional.of(owner));
        given(userGateway.findAllById(employees)).willReturn(List.of());

        assertThatThrownBy(()-> createRestaurantUseCase.execute(createRestaurantInput))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("employee(s) not found");

        then(loggedUserGateway).should().hasRole(Restaurant.CREATE_RESTAURANT);
        then(restaurantGateway).should().existsRestaurantWithName(restaurantName);
        then(userGateway).should().findById(owner.getUuid());
        then(userGateway).should().findAllById(employees);
        then(restaurantGateway).shouldHaveNoMoreInteractions();
        then(createRestaurantNotifierGateway).shouldHaveNoInteractions();
    }
}