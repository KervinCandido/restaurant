package br.com.fiap.restaurant.restaurant.core.controller;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.inbound.*;
import br.com.fiap.restaurant.restaurant.core.outbound.*;
import br.com.fiap.restaurant.restaurant.core.presenter.MenuItemPresenter;
import br.com.fiap.restaurant.restaurant.core.presenter.OpeningHoursPresenter;
import br.com.fiap.restaurant.restaurant.core.usecase.restaurant.*;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import br.com.fiap.restaurant.restaurant.utils.core.OpeningHoursBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RestaurantController")
class RestaurantControllerTest {

    @Mock
    private RestaurantUseCaseFacade restaurantUseCaseFacade;

    @InjectMocks
    private RestaurantController restaurantController;

    @Captor
    private ArgumentCaptor<CreateRestaurantInput> createRestaurantInputCaptor;

    @Captor
    private ArgumentCaptor<UpdateRestaurantInput> updateRestaurantInputCaptor;

    @Captor
    private ArgumentCaptor<PagedQuery<String>> pagedQueryCaptor;

    @Captor
    private ArgumentCaptor<PagedQuery<Void>> pagedQueryVoidCaptor;

    private Long restaurantId;
    private UUID ownerId;
    private UUID employeeUuid;
    private User owner;
    private User employee;
    private OpeningHoursInput openingHoursInput;
    private OpeningHours openingHours;
    private OpeningHoursOutput openingHoursOutput;
    private MenuItemInput menuItemInput;
    private MenuItemOutput menuOutput;
    private MenuItem menuItem;
    private Address address;
    private AddressInput addressInput;
    private AddressOutput addressOutput;

    @BeforeEach
    void setUp() {
        var openingHoursBuilder = OpeningHoursBuilder.builder();
        var menuItemBuilder = MenuItemBuilder.builder();

        ownerId = UUID.randomUUID();
        employeeUuid = UUID.randomUUID();
        restaurantId = 1L;

        openingHoursInput = openingHoursBuilder.buildInput();
        openingHours = openingHoursBuilder.build();
        openingHoursOutput = OpeningHoursPresenter.toOutput(openingHours);

        menuItemInput = menuItemBuilder.buildInput();
        menuItem = menuItemBuilder.build();
        menuOutput = MenuItemPresenter.toOutput(menuItem);

        owner = new User(ownerId, Set.of(User.RESTAURANT_OWNER));
        employee = new User(employeeUuid, Set.of(Restaurant.VIEW_RESTAURANT_MANAGEMENT));
        address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        addressInput = new AddressInput("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        addressOutput = new AddressOutput("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
    }

    @Test
    @DisplayName("Deve criar Restaurant com sucesso e retornar RestaurantPublicOutput")
    void shouldCreateRestaurantSuccessfully() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                "Restaurante Teste",
                addressInput,
                "Italiana",
                Set.of(openingHoursInput),
                Set.of(menuItemInput),
                Set.of(employeeUuid),
                ownerId
        );

        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );

        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(employee);

        given(restaurantUseCaseFacade.createRestaurant(input)).willReturn(restaurant);

        // Act
        RestaurantManagementOutput result = restaurantController.createRestaurant(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(restaurant.getId());
        assertThat(result.name()).isEqualTo(restaurant.getName());
        assertThat(result.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.address()).isEqualTo(addressOutput);
        assertThat(result.openingHours()).isNotNull().hasSize(1).containsExactlyInAnyOrder(openingHoursOutput);
        assertThat(result.menu()).isNotNull().hasSize(1).containsExactlyInAnyOrder(menuOutput);

        then(restaurantUseCaseFacade).should().createRestaurant(createRestaurantInputCaptor.capture());
        CreateRestaurantInput capturedInput = createRestaurantInputCaptor.getValue();
        assertThat(capturedInput).isNotNull();
        assertThat(capturedInput).usingRecursiveComparison().isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CreateRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenUseCaseThrowsException() {
        CreateRestaurantInput input = new CreateRestaurantInput(
                "Restaurante Teste",
                addressInput,
                "Italiana",
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet(),
                UUID.randomUUID()
        );
        RuntimeException expectedException = new RuntimeException("Error creating restaurant");

        given(restaurantUseCaseFacade.createRestaurant(input)).willThrow(expectedException);

        assertThatThrownBy(() -> restaurantController.createRestaurant(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error creating restaurant");

        then(restaurantUseCaseFacade).should().createRestaurant(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar createRestaurant com input nulo")
    void shouldThrowExceptionWhenCreateRestaurantInputIsNull() {
        assertThatThrownBy(() -> restaurantController.createRestaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantInput cannot be null.");
    }

    @Test
    @DisplayName("Deve atualizar Restaurant com sucesso")
    void shouldUpdateRestaurantSuccessfully() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                1L,
                "Novo Nome",
                addressInput,
                "Japonesa",
                null,
                null,
                null,
                null
        );

        restaurantController.updateRestaurant(input);

        then(restaurantUseCaseFacade).should().updateRestaurant(updateRestaurantInputCaptor.capture());
        UpdateRestaurantInput capturedInput = updateRestaurantInputCaptor.getValue();
        assertThat(capturedInput).usingRecursiveComparison().isEqualTo(input);
    }

    @Test
    @DisplayName("Deve lançar exceção quando UpdateRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenUpdateUseCaseThrowsException() {
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Novo Nome",
                addressInput,
                "Japonesa",
                null,
                null,
                null,
                null
        );
        RuntimeException expectedException = new RuntimeException("Error updating restaurant");

        willThrow(expectedException).given(restaurantUseCaseFacade).updateRestaurant(input);

        assertThatThrownBy(() -> restaurantController.updateRestaurant(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error updating restaurant");

        then(restaurantUseCaseFacade).should().updateRestaurant(input);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar updateRestaurant com input nulo")
    void shouldThrowExceptionWhenUpdateRestaurantInputIsNull() {
        assertThatThrownBy(() -> restaurantController.updateRestaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateRestaurantInput cannot be null.");
    }

    @Test
    @DisplayName("Deve buscar Restaurant por ID com sucesso e retornar RestaurantPublicOutput")
    void shouldFindRestaurantByIdSuccessfully() {
        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(employee);

        given(restaurantUseCaseFacade.findById(restaurantId)).willReturn(Optional.of(restaurant));

        var optionalResult = restaurantController.findById(restaurantId);

        assertThat(optionalResult).isNotEmpty();
        var result = optionalResult.get();
        assertThat(result.id()).isEqualTo(restaurant.getId());
        assertThat(result.name()).isEqualTo(restaurant.getName());
        assertThat(result.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.openingHours()).isNotNull().hasSize(1);
        assertThat(result.menuItems()).isNotNull().hasSize(1);

        then(restaurantUseCaseFacade).should().findById(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando GetByIdRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenGetByIdUseCaseThrowsException() {
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Restaurant not found");

        given(restaurantUseCaseFacade.findById(id)).willThrow(expectedException);

        assertThatThrownBy(() -> restaurantUseCaseFacade.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Restaurant not found");

        then(restaurantUseCaseFacade).should().findById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar findById com ID nulo")
    void shouldThrowExceptionWhenFindByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }

    @Test
    @DisplayName("Deve buscar todos os restaurantes com sucesso e retornar lista de RestaurantPublicOutput")
    void shouldFindAllRestaurantsSuccessfully() {
        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);
        restaurant.addEmployee(employee);

        given(restaurantUseCaseFacade.findAll()).willReturn(List.of(restaurant));

        List<RestaurantPublicOutput> result = restaurantController.findAll();

        assertThat(result).isNotNull().hasSize(1);
        RestaurantPublicOutput output = result.getFirst();
        assertThat(output.id()).isEqualTo(restaurant.getId());
        assertThat(output.name()).isEqualTo(restaurant.getName());
        assertThat(output.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(output.openingHours()).isNotNull().hasSize(1);
        assertThat(output.menuItems()).isNotNull().hasSize(1);

        then(restaurantUseCaseFacade).should().findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver restaurantes")
    void shouldReturnEmptyListWhenNoRestaurantsFound() {
        given(restaurantUseCaseFacade.findAll()).willReturn(Collections.emptyList());

        List<RestaurantPublicOutput> result = restaurantController.findAll();

        assertThat(result).isNotNull().isEmpty();

        then(restaurantUseCaseFacade).should().findAll();
    }

    @Test
    @DisplayName("Deve buscar todos os restaurantes paginados com sucesso")
    void shouldFindAllRestaurantsPagedSuccessfully() {
        int pageNumber = 0;
        int pageSize = 10;

        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);

        Page<Restaurant> restaurantPage = new Page<>(pageNumber, pageSize, 1L, List.of(restaurant));

        given(restaurantUseCaseFacade.findAll(any(PagedQuery.class))).willReturn(restaurantPage);

        Page<RestaurantPublicOutput> result = restaurantController.findAll(pageNumber, pageSize);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).hasSize(1);

        RestaurantPublicOutput output = result.content().getFirst();
        assertThat(output.id()).isEqualTo(restaurant.getId());
        assertThat(output.name()).isEqualTo(restaurant.getName());
        assertThat(output.cuisineType()).isEqualTo(restaurant.getCuisineType());

        then(restaurantUseCaseFacade).should().findAll(pagedQueryVoidCaptor.capture());
        PagedQuery<Void> capturedQuery = pagedQueryVoidCaptor.getValue();
        assertThat(capturedQuery.pageNumber()).isEqualTo(pageNumber);
        assertThat(capturedQuery.pageSize()).isEqualTo(pageSize);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver restaurantes paginados")
    void shouldReturnEmptyPageWhenNoRestaurantsFoundPaged() {
        int pageNumber = 0;
        int pageSize = 10;
        Page<Restaurant> emptyPage = new Page<>(pageNumber, pageSize, 0L, List.of());

        given(restaurantUseCaseFacade.findAll(any(PagedQuery.class))).willReturn(emptyPage);

        Page<RestaurantPublicOutput> result = restaurantController.findAll(pageNumber, pageSize);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.content()).isEmpty();

        then(restaurantUseCaseFacade).should().findAll(any(PagedQuery.class));
    }

    @Test
    @DisplayName("Deve buscar por tipo de cozinha e retornar página de RestaurantPublicOutput")
    void shouldFindByCuisineTypeAndReturnPageOfRestaurantOutput() {
        String cuisineType = "Italiana";
        int pageNumber = 0;
        int pageSize = 10;

        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);

        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                cuisineType,
                owner
        );
        restaurant.addOpeningHours(openingHours);
        restaurant.addMenuItem(menuItem);

        Page<Restaurant> restaurantPage = new Page<>(pageNumber, pageSize, 1L, List.of(restaurant));

        given(restaurantUseCaseFacade.findByCuisineType(pagedQuery)).willReturn(restaurantPage);

        Page<RestaurantPublicOutput> result = restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).hasSize(1);

        RestaurantPublicOutput output = result.content().getFirst();
        assertThat(output.cuisineType()).isEqualTo(cuisineType);
        assertThat(output.address()).isEqualTo(addressOutput);
        assertThat(output.openingHours()).containsExactlyInAnyOrder(openingHoursOutput);
        assertThat(output.menuItems()).containsExactlyInAnyOrder(menuOutput);

        then(restaurantUseCaseFacade).should().findByCuisineType(pagedQueryCaptor.capture());
        assertThat(pagedQueryCaptor.getValue()).usingRecursiveComparison().isEqualTo(pagedQuery);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum restaurante for encontrado por tipo de cozinha")
    void shouldReturnEmptyPageWhenNoRestaurantFoundByCuisineType() {
        String cuisineType = "Inexistente";
        int pageNumber = 0;
        int pageSize = 10;

        var pagedQuery = new PagedQuery<>(cuisineType, pageNumber, pageSize);
        Page<Restaurant> emptyPage = new Page<>(pageNumber, pageSize, 0L, List.of());

        given(restaurantUseCaseFacade.findByCuisineType(pagedQuery)).willReturn(emptyPage);

        Page<RestaurantPublicOutput> result = restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize);

        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
        assertThat(result.content()).isEmpty();

        then(restaurantUseCaseFacade).should().findByCuisineType(pagedQuery);
    }

    @Test
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        Long id = 1L;

        restaurantController.deleteById(id);

        then(restaurantUseCaseFacade).should().deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar deleteById com ID nulo")
    void shouldThrowExceptionWhenDeleteByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.deleteById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando DeleteRestaurantUseCase lança exceção")
    void shouldThrowExceptionWhenDeleteUseCaseThrowsException() {
        Long id = 1L;
        RuntimeException expectedException = new RuntimeException("Error deleting restaurant");

        willThrow(expectedException).given(restaurantUseCaseFacade).deleteById(id);

        assertThatThrownBy(() -> restaurantController.deleteById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error deleting restaurant");

        then(restaurantUseCaseFacade).should().deleteById(id);
    }

    @Test
    @DisplayName("Deve buscar Restaurant Management por ID com sucesso e retornar RestaurantManagementOutput")
    void shouldFindRestaurantManagementByIdSuccessfully() {
        Long id = 1L;

        Restaurant restaurant = new Restaurant(
                restaurantId,
                "Restaurante Teste",
                address,
                "Italiana",
                owner
        );
        restaurant.addEmployee(new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT)));

        given(restaurantUseCaseFacade.findManagementById(id)).willReturn(Optional.of(restaurant));

        var result = restaurantController.findManagementById(id);

        assertThat(result).isNotEmpty();
        assertThat(result.get().id()).isEqualTo(id);
        assertThat(result.get().employees()).isNotNull().hasSize(1);

        then(restaurantUseCaseFacade).should().findManagementById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao chamar findManagementById com ID nulo")
    void shouldThrowExceptionWhenFindManagementByIdWithNullId() {
        assertThatThrownBy(() -> restaurantController.findManagementById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Restaurant Id cannot be null.");
    }
}
