package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateMenuItemInput;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateMenuItemUseCase")
class UpdateMenuItemUseCaseTest {

    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private PublisherGateway<MenuItem> updateMenuItemPublisher;

    @Captor
    private ArgumentCaptor<MenuItem> menuItemCaptor;

    @InjectMocks
    private UpdateMenuItemUseCase useCase;
    private Restaurant restaurant;
    private User owner;
    private Long restaurantId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        // Arrange
        itemId = 10L;
        restaurantId = 5L;

        UUID ownerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        owner = new User(ownerId, Set.of(User.RESTAURANT_OWNER));

        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");

        restaurant = new Restaurant(restaurantId, "Current Restaurant", address, "Italiana", owner);
    }

    @Test
    @DisplayName("Deve atualizar item de menu com sucesso")
    void shouldUpdateMenuItemSuccessfully() {
        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withDescription("Old desc")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("  Pizza  ")
                .withDescription("  Nova descrição  ")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("  /photos/pizza.jpg  ")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId)))
                .willAnswer(invocation -> invocation.getArgument(0, MenuItem.class));

        // Act
        MenuItem result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);

        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem toSave = menuItemCaptor.getValue();

        assertThat(toSave.getId()).isEqualTo(itemId);
        assertThat(toSave.getName()).isEqualTo("Pizza");
        assertThat(toSave.getDescription()).isEqualTo("Nova descrição");
        assertThat(toSave.getPrice()).isEqualByComparingTo("25.00");
        assertThat(toSave.getRestaurantOnly()).isTrue();
        assertThat(toSave.getPhotoPath()).isEqualTo("/photos/pizza.jpg");

        then(loggedUserGateway).should().hasRole(MenuItem.UPDATE_MENU_ITEM);
        then(loggedUserGateway).should().requireCurrentUser();
        then(updateMenuItemPublisher).should().publish(result);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando não tiver permissão")
    void shouldThrowOperationNotAllowedWhenNoPermission() {
        // Arrange
        UpdateMenuItemInput input = MenuItemBuilder.builder().withId(10L).buildUpdateInput();
        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("does not have permission");

        then(loggedUserGateway).should().hasRole(MenuItem.UPDATE_MENU_ITEM);
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando item não existir")
    void shouldThrowBusinessExceptionWhenMenuItemNotFound() {
        // Arrange
        UpdateMenuItemInput input = MenuItemBuilder.builder().withId(itemId).buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Item de cardápio não encontrado com ID: " + itemId);

        then(menuItemGateway).should().findById(itemId);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).save(any(), any());
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    // (cobre lambda$doExecute$1 ou $2): restaurantId não encontrado
    @Test
    @DisplayName("Deve lançar BusinessException quando não encontrar restaurantId associado ao item")
    void shouldThrowBusinessExceptionWhenRestaurantIdNotFoundForItem() {
        // Arrange
        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                // use contains pra não travar em variação pequena de texto
                .hasMessageContaining("Restaurante associado");

        then(loggedUserGateway).should().hasRole(MenuItem.UPDATE_MENU_ITEM);
        then(menuItemGateway).should().findById(itemId);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);

        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).save(any(), any());
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    // (cobre o outro lambda): restaurante não encontrado
    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange

        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act / Assert
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurante não encontrado");

        then(loggedUserGateway).should().hasRole(MenuItem.UPDATE_MENU_ITEM);
        then(menuItemGateway).should().findById(itemId);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);
        then(restaurantGateway).should().findById(restaurantId);

        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).save(any(), any());
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não for o dono")
    void shouldThrowOperationNotAllowedWhenCurrentUserIsNotOwner() {
        // Arrange

        UUID otherId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        User otherUser = new User(otherId, Set.of(User.RESTAURANT_OWNER));

        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(otherUser);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("Apenas o dono do restaurante pode atualizar itens do cardápio.");

        then(menuItemGateway).should(times(1)).findById(itemId);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);
        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should(never()).save(any(), any());
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando já existir item com o mesmo nome no restaurante")
    void shouldThrowBusinessExceptionWhenDuplicateName() {
        // Arrange

        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza") // mudou
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(true);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Já existe um item com este nome no restaurante");

        then(menuItemGateway).should().existsByNameAndRestaurantId("Pizza", restaurantId);
        then(menuItemGateway).should(never()).save(any(), any());
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateMenuItemInput cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(updateMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Não deve checar duplicidade quando nome não mudou (newName == oldName)")
    void shouldNotCheckDuplicateWhenNameDidNotChange() {
        // Arrange
        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("  Pizza  ") // <- oldName vai virar "Pizza"
                .withDescription("Old desc")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza") // <- newName "Pizza" == oldName "Pizza"
                .withDescription("Nova")
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg")
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);

        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId)))
                .willAnswer(inv -> inv.getArgument(0, MenuItem.class));

        // Act
        MenuItem menuItem = useCase.execute(input);

        // Assert
        then(menuItemGateway).should(never()).existsByNameAndRestaurantId(any(), any());
        then(menuItemGateway).should().save(any(MenuItem.class), eq(restaurantId));
        then(updateMenuItemPublisher).should().publish(menuItem);
    }

    @Test
    @DisplayName("Deve setar description como null quando input.description é null (ternário do description)")
    void shouldSetNullDescriptionWhenInputDescriptionIsNull() {
        // Arrange

        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withDescription("Old desc")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza")
                .withDescription(null) // <- cobre o branch do description
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath("/photos/pizza.jpg") // <- NÃO pode ser null
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId)))
                .willAnswer(inv -> inv.getArgument(0, MenuItem.class));

        // Act
        MenuItem menuItem = useCase.execute(input);

        // Assert
        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem captured = menuItemCaptor.getValue();

        assertThat(captured.getDescription()).isNull();
        assertThat(captured.getPhotoPath()).isEqualTo("/photos/pizza.jpg");
        then(updateMenuItemPublisher).should().publish(menuItem);
    }

    @Test
    @DisplayName("Deve setar photoPath como null quando input.photoPath é null (ternário do photoPath)")
    void shouldSetNullPhotoPathWhenInputPhotoPathIsNull() {
        // Arrange

        MenuItem existingItem = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Old Name")
                .withDescription("Old desc")
                .withPrice(new BigDecimal("10.00"))
                .withRestaurantOnly(false)
                .withPhotoPath("/old.jpg")
                .build();

        UpdateMenuItemInput input = MenuItemBuilder.builder()
                .withId(itemId)
                .withName("Pizza")
                .withDescription("Nova") // <- NÃO pode ser null aqui, pra não misturar com o teste do description
                .withPrice(new BigDecimal("25.00"))
                .withRestaurantOnly(true)
                .withPhotoPath(null) // <- cobre o branch do photoPath
                .buildUpdateInput();

        given(loggedUserGateway.hasRole(MenuItem.UPDATE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findById(itemId)).willReturn(Optional.of(existingItem));
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);
        given(menuItemGateway.existsByNameAndRestaurantId("Pizza", restaurantId)).willReturn(false);

        given(menuItemGateway.save(any(MenuItem.class), eq(restaurantId)))
                .willAnswer(inv -> inv.getArgument(0, MenuItem.class));

        // Act
        MenuItem menuItem = useCase.execute(input);

        // Assert
        then(menuItemGateway).should().save(menuItemCaptor.capture(), eq(restaurantId));
        MenuItem captured = menuItemCaptor.getValue();

        assertThat(captured.getPhotoPath()).isEqualTo("/old.jpg");
        assertThat(captured.getDescription()).isEqualTo("Nova");
        then(updateMenuItemPublisher).should().publish(menuItem);
    }


}
