package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetAllMenuItemsByRestaurantUseCase")
class GetAllMenuItemsByRestaurantUseCaseTest {

    @Mock private MenuItemGateway menuItemGateway;
    @Mock private RestaurantGateway restaurantGateway;

    @InjectMocks
    private GetAllMenuItemsByRestaurantUseCase useCase;

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("id cannot be null");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 10L;

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurante não encontrado.");

        then(restaurantGateway).should().findById(restaurantId);

        then(menuItemGateway).should(never()).findByRestaurantId(anyLong());
        then(menuItemGateway).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Deve retornar lista de itens quando restaurante existir e usuário tiver permissão")
    void shouldReturnMenuItemsWhenRestaurantExistsAndUserHasRole() {
        // Arrange
        Long restaurantId = 10L;

        User owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        Restaurant restaurant = new Restaurant(restaurantId, "Current Restaurant", address, "Italiana", owner);

        List<MenuItem> expected = List.of(
            MenuItemBuilder.builder().withName("Pizza").build(),
            MenuItemBuilder.builder().withName("Lasanha").build()
        );

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurantId(restaurantId)).willReturn(expected);

        // Act
        List<MenuItem> result = useCase.execute(restaurantId);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(MenuItem::getName)
                .containsExactlyInAnyOrder("Pizza", "Lasanha");

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurantId(restaurantId);

        then(restaurantGateway).shouldHaveNoMoreInteractions();
        then(menuItemGateway).shouldHaveNoMoreInteractions();
    }
}
