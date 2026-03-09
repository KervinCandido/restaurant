package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.exception.RestaurantNameIsAlreadyInUseException;
import br.com.fiap.restaurant.restaurant.core.exception.UserCannotBeRestaurantOwnerException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.AddressInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateMenuItemInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateOpeningHoursInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateRestaurantInput;
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

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateRestaurantUseCase")
class UpdateRestaurantUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private UserGateway userGateway;

    @InjectMocks
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Captor
    private ArgumentCaptor<Restaurant> restaurantCaptor;

    private Long restaurantId;

    private UUID ownerId;
    private User owner;

    private User oldEmployee;

    private Restaurant current;

    private AddressInput addressInput;

    @BeforeEach
    void setUp() {
        // Arrange
        restaurantId = 1L;

        ownerId = UUID.randomUUID();
        owner = new User(ownerId, Set.of(User.RESTAURANT_OWNER));

        var address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        this.addressInput = new AddressInput("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        current = new Restaurant(1L, "Current Restaurant", address, "Italiana", owner);

        Set<OpeningHours> openingHours = new HashSet<>(3);
        openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.THURSDAY).build());
        openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.FRIDAY).build());
        openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.SATURDAY).build());
        current.addOpeningHours(openingHours);

        Set<MenuItem> itemsMenu = new HashSet<>(3);
        itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        current.addMenuItems(itemsMenu);

        UUID oldEmployeeId = UUID.randomUUID();
        oldEmployee = new User(oldEmployeeId, Set.of(Restaurant.VIEW_RESTAURANT));
        current.addEmployee(oldEmployee);
    }

    @Test
    @DisplayName("Deve atualizar restaurante e substituir coleções quando fornecidas")
    void shouldUpdateRestaurantReplacingCollectionsWhenProvided() {
        // Arrange
        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new User(newOwnerId, Set.of(User.RESTAURANT_OWNER));

        UUID newEmployeeId = UUID.randomUUID();
        User newEmployee = new User(newEmployeeId, Set.of(User.RESTAURANT_OWNER));

        UpdateOpeningHoursInput openingHoursInput = OpeningHoursBuilder.builder().buildUpdateInput();
        UpdateMenuItemInput menuItemInput = MenuItemBuilder.builder().buildUpdateInput();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                addressInput,
                "New Cuisine",
                Set.of(openingHoursInput),
                Set.of(menuItemInput),
                Set.of(newEmployeeId),
                newOwnerId
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner); // pode gerenciar (é dono)
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(false);
        given(userGateway.findById(newEmployeeId)).willReturn(Optional.of(newEmployee));

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getId()).isEqualTo(restaurantId);
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getCuisineType()).isEqualTo("New Cuisine");
        assertThat(saved.getOwner()).isEqualTo(newOwner);

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(newEmployee);
        assertThat(saved.getOpeningHours()).hasSize(1);
        assertThat(saved.getMenuItems()).hasSize(1);

        then(loggedUserGateway).should().hasRole(Restaurant.UPDATE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(userGateway).should().findById(newOwnerId);
        then(restaurantGateway).should().existsRestaurantWithName("New Name");
        then(userGateway).should().findById(newEmployeeId);
    }

    @Test
    @DisplayName("Deve manter employees atuais quando employees no input for null (PATCH semantics)")
    void shouldKeepEmployeesWhenEmployeesIsNull() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null, // employees null => mantém
                null  // ownerId null => mantém ownerId atual
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getEmployees()).containsExactlyInAnyOrder(oldEmployee);
        assertThat(saved.getOwner()).isEqualTo(owner);

        // nome não mudou -> NÃO deve consultar existsRestaurantWithName
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());

        // employees null -> NÃO consulta employee no gateway
        then(userGateway).should(times(1)).findById(ownerId);
        then(userGateway).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando employee não existir e não salvar")
    void shouldThrowWhenEmployeeNotFound() {
        // Arrange
        UUID missingEmployeeId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                Set.of(missingEmployeeId),
                null
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(userGateway.findById(missingEmployeeId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Employee " + missingEmployeeId + " not found.");

        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(false);

        // Act & Assert
        var updateRestaurantInput = new UpdateRestaurantInput(restaurantId, "x", null, "y", null, null, null, null);
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(updateRestaurantInput))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("permission to perform this action");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário logado não é ownerId nem employee")
    void shouldThrowOperationNotAllowedWhenCurrentUserCannotManage() {
        // Arrange
        User outsider = new User(UUID.randomUUID(), Set.of());

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Any",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(outsider);

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("permission to perform this action");

        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar RestaurantNameIsAlreadyInUseException quando nome mudar e já existir")
    void shouldThrowWhenRestaurantNameAlreadyInUse() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "New Name",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(ownerId)).willReturn(Optional.of(owner));
        given(restaurantGateway.existsRestaurantWithName("New Name")).willReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(RestaurantNameIsAlreadyInUseException.class);

        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando novo ownerId não for encontrado")
    void shouldThrowWhenNewOwnerNotFound() {
        // Arrange
        UUID newOwnerId = UUID.randomUUID();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null,
                newOwnerId
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Owner not found.");

        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar UserCannotBeRestaurantOwnerException quando novo ownerId não pode ser dono")
    void shouldThrowWhenUserCannotBeOwner() {
        // Arrange
        UUID newOwnerId = UUID.randomUUID();
        User newOwner = new User(newOwnerId, Set.of(Restaurant.VIEW_RESTAURANT));

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                null,
                newOwnerId
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(newOwnerId)).willReturn(Optional.of(newOwner));

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(UserCannotBeRestaurantOwnerException.class);

        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for null (UseCaseBase)")
    void shouldThrowWhenInputIsNull() {
        // Arrange / Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(userGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não for encontrado (cobre lambda do orElseThrow)")
    void shouldThrowWhenRestaurantNotFound() {
        // Arrange
        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                "Any",
                null,
                null,
                null,
                null,
                null,
                null
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateRestaurantUseCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found.");

        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(userGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should(never()).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve reutilizar cache quando ownerId também está na lista de employees (não consulta userGateway 2x)")
    void shouldNotQueryUserGatewayTwiceWhenOwnerIsAlsoEmployee() {
        // Arrange
        UUID sameOwnerId = owner.getUuid();

        UpdateRestaurantInput input = new UpdateRestaurantInput(
                restaurantId,
                null,
                null,
                null,
                null,
                null,
                Set.of(sameOwnerId), // employee == ownerId (cache)
                sameOwnerId          // novo ownerId == ownerId atual
        );

        given(loggedUserGateway.hasRole(Restaurant.UPDATE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(current));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(userGateway.findById(sameOwnerId)).willReturn(Optional.of(owner)); // só 1 vez

        // Act
        updateRestaurantUseCase.execute(input);

        // Assert
        then(restaurantGateway).should().save(restaurantCaptor.capture());
        Restaurant saved = restaurantCaptor.getValue();

        assertThat(saved.getOwner().getUuid()).isEqualTo(sameOwnerId);
        assertThat(saved.getEmployees()).contains(owner); // agora employee inclui ownerId

        then(userGateway).should(times(1)).findById(sameOwnerId);
        then(restaurantGateway).should(never()).existsRestaurantWithName(anyString());
    }
}
