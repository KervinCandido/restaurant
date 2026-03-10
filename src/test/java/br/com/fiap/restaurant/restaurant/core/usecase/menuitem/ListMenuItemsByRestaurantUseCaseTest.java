package br.com.fiap.restaurant.restaurant.core.usecase.menuitem;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListMenuItemsByRestaurantUseCase")
class ListMenuItemsByRestaurantUseCaseTest {

    @Mock
    private MenuItemGateway menuItemGateway;
    @Mock
    private RestaurantGateway restaurantGateway;

    @Captor
    private ArgumentCaptor<PagedQuery<Long>> pagedQueryCaptor;

    @InjectMocks
    private ListMenuItemsByRestaurantUseCase listMenuItemsByRestaurantUseCase;

    @Test
    @DisplayName("Deve retornar página de itens de menu com sucesso")
    void shouldReturnPageOfMenuItemsSuccessfully() {
        // Arrange
        Long restaurantId = 1L;
        User owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        Restaurant restaurant = new Restaurant(restaurantId, "Current Restaurant", address, "Italiana", owner);


        int pageNumber = 0;
        int pageSize = 1;
        long totalElements = 10L;

        MenuItem menuItem = MenuItemBuilder.builder().build();
        var query = new PagedQuery<>(restaurantId, pageNumber, pageSize);

        Page<MenuItem> expectedPage = new Page<>(pageNumber, pageSize, totalElements, List.of(menuItem));

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurant(query)).willReturn(expectedPage);

        // Act
        Page<MenuItem> result = listMenuItemsByRestaurantUseCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(10); // ceil(10/1) = 10 (derivado)
        assertThat(result.content()).containsExactly(menuItem);

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurant(pagedQueryCaptor.capture());

        assertThat(pagedQueryCaptor.getValue()).isEqualTo(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver itens de menu")
    void shouldReturnEmptyPageWhenNoMenuItems() {
        // Arrange
        Long restaurantId = 1L;
        User owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        Restaurant restaurant = new Restaurant(restaurantId, "Current Restaurant", address, "Italiana", owner);


        int pageNumber = 0;
        int pageSize = 1;
        long totalElements = 0L;

        var query = new PagedQuery<>(restaurantId, pageNumber, pageSize);
        Page<MenuItem> expectedPage = new Page<>(pageNumber, pageSize, totalElements, List.of());

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuItemGateway.findByRestaurant(query)).willReturn(expectedPage);

        // Act
        Page<MenuItem> result = listMenuItemsByRestaurantUseCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // totalElements=0 => 0 (derivado)
        assertThat(result.content()).isEmpty();

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should().findByRestaurant(pagedQueryCaptor.capture());

        assertThat(pagedQueryCaptor.getValue()).isEqualTo(query);
    }

    @Test
    @DisplayName("Deve lançar exceção quando restaurante não for encontrado")
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Arrange
        Long restaurantId = 1L;
        var query = new PagedQuery<>(restaurantId, 0, 10);

        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> listMenuItemsByRestaurantUseCase.execute(query))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Restaurant not found");

        then(restaurantGateway).should().findById(restaurantId);
        then(menuItemGateway).should(never()).findByRestaurant(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o input for nulo")
    void shouldThrowExceptionWhenInputIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> listMenuItemsByRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("filter cannot be null");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(menuItemGateway).shouldHaveNoInteractions();
    }

}