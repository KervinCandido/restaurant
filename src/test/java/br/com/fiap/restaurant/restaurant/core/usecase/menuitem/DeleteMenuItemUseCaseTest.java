package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.event.MenuItemEvent;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteMenuItemUseCase")
class DeleteMenuItemUseCaseTest {

    @Mock private LoggedUserGateway loggedUserGateway;
    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;
    @Mock private PublisherGateway<MenuItemEvent> deleteMenuItemPublisher;

    @InjectMocks
    private DeleteMenuItemUseCase useCase;

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
        restaurant.addMenuItem(MenuItemBuilder.builder().withId(itemId).build());

    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(deleteMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando não tem role (UseCaseBase)")
    void shouldThrowOperationNotAllowedWhenUserHasNoRole() {
        // Arrange
        given(loggedUserGateway.hasRole(MenuItem.DELETE_MENU_ITEM)).willReturn(false);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(10L))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission to perform this action.");

        then(loggedUserGateway).should().hasRole(MenuItem.DELETE_MENU_ITEM);
        then(loggedUserGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
        then(deleteMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando não encontrar restaurantId associado ao item")
    void shouldThrowBusinessExceptionWhenRestaurantIdNotFoundForItem() {
        // Arrange

        given(loggedUserGateway.hasRole(MenuItem.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante associado não encontrado");

        then(loggedUserGateway).should().hasRole(MenuItem.DELETE_MENU_ITEM);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);

        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).deleteById(anyLong());
        then(deleteMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange
        Long invalidRestaurantId = 99L;

        given(loggedUserGateway.hasRole(MenuItem.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(invalidRestaurantId));
        given(restaurantGateway.findById(invalidRestaurantId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante não encontrado com ID: " + invalidRestaurantId);

        then(loggedUserGateway).should().hasRole(MenuItem.DELETE_MENU_ITEM);
        then(menuItemGateway).should().findRestaurantIdByItemId(itemId);
        then(restaurantGateway).should().findById(invalidRestaurantId);

        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(menuItemGateway).should(never()).deleteById(anyLong());
        then(deleteMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve negar quando usuário não for o dono (comparando por UUID)")
    void shouldThrowOperationNotAllowedWhenUserIsNotOwner() {
        // Arrange

        UUID currentUserId = UUID.randomUUID();
        User currentUser = new User(currentUserId, Set.of(Restaurant.VIEW_RESTAURANT));

        given(loggedUserGateway.hasRole(MenuItem.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(itemId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Apenas o dono do restaurante pode deletar itens do cardápio.");

        then(menuItemGateway).should(never()).deleteById(anyLong());
        then(deleteMenuItemPublisher).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve deletar item com sucesso quando usuário for dono e tiver permissão")
    void shouldDeleteSuccessfullyWhenUserIsOwnerAndHasRole() {
        // Arrange

        given(loggedUserGateway.hasRole(MenuItem.DELETE_MENU_ITEM)).willReturn(true);
        given(menuItemGateway.findRestaurantIdByItemId(itemId)).willReturn(Optional.of(restaurantId));
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(restaurant.getOwner());

        // Act
        useCase.execute(itemId);

        // Assert
        then(menuItemGateway).should().deleteById(itemId);
        then(deleteMenuItemPublisher).should().publish(any(MenuItemEvent.class));
    }
}