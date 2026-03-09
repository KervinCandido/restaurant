package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListRestaurantsPagedUseCase")
class ListRestaurantsPagedUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsPagedUseCase useCase;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        Long restaurantId = 1L;
        User owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        var address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        restaurant = new Restaurant(restaurantId, "Current Restaurant", address, "Italiana", owner);
    }
    
    @Test
    @DisplayName("Deve retornar página de restaurantes quando usuário tiver permissão")
    void shouldReturnPageOfRestaurantsWhenUserHasPermission() {
        // Arrange
        int currentPage = 0;
        int pageSize = 10;

        PagedQuery<Void> query = new PagedQuery<>(null, currentPage, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                currentPage,
                pageSize,
                1L,
                List.of(restaurant)
        );

        given(restaurantGateway.findAll(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(currentPage);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isOne();
        assertThat(result.totalPages()).isOne();
        assertThat(result.content()).isNotNull().hasSize(1).containsExactly(restaurant);

        then(loggedUserGateway).should(never()).hasRole(Restaurant.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver restaurantes e usuário tiver permissão")
    void shouldReturnEmptyPageWhenNoRestaurantsAndUserHasPermission() {
        // Arrange
        int currentPage = 0;
        int pageSize = 10;

        PagedQuery<Void> query = new PagedQuery<>(null, currentPage, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                currentPage,
                pageSize,
                0L,
                List.of()
        );

        given(restaurantGateway.findAll(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // com totalElements=0, tende a ser 0
        assertThat(result.content()).isEmpty();

        then(loggedUserGateway).should(never()).hasRole(Restaurant.VIEW_RESTAURANT);
        then(restaurantGateway).should().findAll(query);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando query for nula (validação do UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenQueryIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query cannot be null.");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
