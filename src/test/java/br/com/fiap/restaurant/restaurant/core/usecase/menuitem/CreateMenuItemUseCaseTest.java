package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.OpeningHours;
import br.com.fiap.restaurant.restaurant.core.event.MenuItemEvent;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateMenuItemInput;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateMenuItemUseCase")
class CreateMenuItemUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private PublisherGateway<MenuItemEvent> menuItemCreatePublisher;

    @InjectMocks
    private CreateMenuItemUseCase useCase;

    @Captor
    private ArgumentCaptor<MenuItem> menuItemCaptor;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        var ownerId = UUID.randomUUID();
        var owner = new User(ownerId, Set.of(User.RESTAURANT_OWNER));

        var address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        restaurant = new Restaurant(10L, "Current Restaurant", address, "Italiana", owner);

        Set<OpeningHours> openingHours = new HashSet<>(3);
        openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.THURSDAY).build());
        openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.FRIDAY).build());
        openingHours.add(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.SATURDAY).build());
        restaurant.addOpeningHours(openingHours);

        Set<MenuItem> itemsMenu = new HashSet<>(3);
        itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        itemsMenu.add(MenuItemBuilder.builder().withoutId().build());
        restaurant.addMenuItems(itemsMenu);

        UUID oldEmployeeId = UUID.randomUUID();
        var oldEmployee = new User(oldEmployeeId, Set.of(Restaurant.VIEW_RESTAURANT));
        restaurant.addEmployee(oldEmployee);
    }

    private MenuItemBuilder builder() {
        return MenuItemBuilder.builder()
                .withName("Pizza")
                .withDescription("Pizza clássica")
                .withPrice(new BigDecimal("30"))
                .withRestaurantOnly(false)
                .withPhotoPath("/photos/pizza.jpg");
    }

    /**
     * Helper: configura o fluxo para passar por:
     * - role ok
     * - restaurante existe
     * - currentUser existe
     * - ownerId == currentUserId
     *
     * IMPORTANTE: chame este helper somente nos testes que realmente precisam
     * passar pela validação de ownerId, senão o Mockito vai acusar stubs não usados.
     */
    private Restaurant arrangeAuthorizedOwnerFlow(Long restaurantId) {

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(restaurant.getOwner());

        return restaurant;
    }

    @Test
    @DisplayName("Deve criar item com sucesso quando usuário tem role e é o dono (ownerId por UUID)")
    void shouldCreateSuccessfullyWhenUserHasRoleAndIsOwner() {
        // Arrange
        Long restaurantId = 10L;

        arrangeAuthorizedOwnerFlow(restaurantId);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        MenuItem saved = new MenuItem(
                123L, "Pizza", "Pizza clássica", new BigDecimal("30"), false, "/photos/pizza.jpg"
        );
        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId))).willReturn(saved);

        CreateMenuItemInput input = builder().buildCreateMenuInput(restaurantId);

        // Act
        MenuItem result = useCase.execute(input);

        // Assert
        assertThat(result.getId()).isEqualTo(123L);

        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem captured = menuItemCaptor.getValue();
        assertThat(captured.getId()).isNull();
        assertThat(captured.getName()).isEqualTo("Pizza");
        assertThat(captured.getPhotoPath()).isEqualTo("/photos/pizza.jpg");

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(loggedUserGateway).should().hasRole(MenuItem.CREATE_MENU_ITEM);
        then(menuItemCreatePublisher).should().publish(any(MenuItemEvent.class));
    }

    @Test
    @DisplayName("Deve negar quando não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(false);

        CreateMenuItemInput input = builder().buildCreateMenuInput(1L);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should().hasRole(MenuItem.CREATE_MENU_ITEM);
        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 99L;

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        CreateMenuItemInput input = builder()
                .buildCreateMenuInput(restaurantId);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante não encontrado com ID: " + restaurantId);

        then(loggedUserGateway).should().hasRole(MenuItem.CREATE_MENU_ITEM);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando usuário não é o dono (ownerId != currentUserId)")
    void shouldThrowOperationNotAllowedWhenUserIsNotOwner() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        UUID ownerId = UUID.randomUUID();
        UUID currentId = UUID.randomUUID();

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getUuid()).willReturn(ownerId);
        given(currentUser.getUuid()).willReturn(currentId);

        CreateMenuItemInput input = builder()
                .buildCreateMenuInput(restaurantId);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode criar itens do cardápio.");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando restaurante não tem ownerId (ownerId = null)")
    void shouldThrowOperationNotAllowedWhenRestaurantOwnerIsNull() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = mock(Restaurant.class);
        User currentUser = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        given(restaurant.getOwner()).willReturn(null); // <- cobre ownerId == null
        given(currentUser.getUuid()).willReturn(UUID.randomUUID());

        CreateMenuItemInput input = builder()
                .buildCreateMenuInput(restaurantId);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode criar itens do cardápio.");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemGateway).should(never()).save(any(), any());
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }


    @Test
    @DisplayName("Deve lançar BusinessException quando name for blank")
    void shouldThrowBusinessExceptionWhenNameBlank() {
        // Arrange
        Long restaurantId = 10L;

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(mock(Restaurant.class)));

        // NOTE: sem stubs de ownerId/user, porque falha antes.
        CreateMenuItemInput input = builder()
                .withName("   ")
                .buildCreateMenuInput(restaurantId);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("name cannot be blank");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Input cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando name é nulo")
    void shouldThrowNullPointerExceptionWhenNameIsNull() {
        // Arrange
        Long restaurantId = 10L;

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(mock(Restaurant.class)));

        // NOTE: sem stubs de ownerId/user, porque explode em Objects.requireNonNull(input.name()) antes.
        CreateMenuItemInput input = builder()
                .withName(null)
                .buildCreateMenuInput(restaurantId);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name cannot be null");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando nome duplicado no restaurante")
    void shouldThrowBusinessExceptionWhenDuplicatedNameInRestaurant() {
        // Arrange
        Long restaurantId = restaurant.getId();

        CreateMenuItemInput input = builder()
                .withName("  Pizza  ")
                .buildCreateMenuInput(restaurantId);

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(loggedUserGateway.requireCurrentUser()).willReturn(restaurant.getOwner());
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe um item de cardápio com o nome");

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(menuItemGateway).should(never()).save(any(), any());
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando usuário atual for nulo (currentUserId = null)")
    void shouldThrowOperationNotAllowedWhenCurrentUserIsNull() {
        // Arrange
        Long restaurantId = 10L;

        Restaurant restaurant = mock(Restaurant.class);
        User owner = mock(User.class);

        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(null); // <- cobre currentUserId == null

        given(restaurant.getOwner()).willReturn(owner);
        given(owner.getUuid()).willReturn(UUID.randomUUID());

        CreateMenuItemInput input = builder()
                .buildCreateMenuInput(restaurantId);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode criar itens do cardápio.");

        then(menuItemGateway).shouldHaveNoInteractions();
        then(menuItemGateway).should(never()).save(any(), any());
        then(menuItemCreatePublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve permitir description nula (description = null)")
    void shouldAllowNullDescription() {
        // Arrange
        Long restaurantId = 10L;

        MenuItemBuilder menuItemBuilder = builder().withDescription(null);
        CreateMenuItemInput input = menuItemBuilder // <- cobre o ramo input.description() == null
                .buildCreateMenuInput(restaurantId);

        MenuItem menuItem = menuItemBuilder.withId(2L).build();
        restaurant.addMenuItem(menuItem);


        given(loggedUserGateway.hasRole(MenuItem.CREATE_MENU_ITEM)).willReturn(true);
        given(loggedUserGateway.requireCurrentUser()).willReturn(restaurant.getOwner());
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // retorno do save (pode ser qualquer objeto)
        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId))).willReturn(menuItem);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));

        // Act
        MenuItem createdMenuItem = useCase.execute(input);

        // Assert
        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem captured = menuItemCaptor.getValue();

        assertThat(captured.getDescription()).isNull(); // <- valida o ramo do ternário

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(loggedUserGateway).should().hasRole(MenuItem.CREATE_MENU_ITEM);
        then(menuItemCreatePublisher).should().publish(new MenuItemEvent(restaurant.getId(), createdMenuItem));
    }


}