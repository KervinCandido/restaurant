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

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ListRestaurantsByCuisineTypeUseCase")
class ListRestaurantsByCuisineTypeUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private ListRestaurantsByCuisineTypeUseCase useCase;

    private Long restaurantId;
    private Restaurant restaurant;
    private User owner;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        var address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        restaurant = new Restaurant(restaurantId, "Current Restaurant", address, "Italiana", owner);
    }

    @Test
    @DisplayName("Deve retornar página de restaurantes com sucesso")
    void shouldReturnPageOfRestaurantsSuccessfully() {
        // Arrange
        String cuisineType = "Italian";

        int pageNumber = 0;
        int pageSize = 10;
        long totalElements = 1L;

        PagedQuery<String> query = new PagedQuery<>(cuisineType, pageNumber, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                pageNumber,
                pageSize,
                totalElements,
                List.of(restaurant)
        );

        given(restaurantGateway.findByCuisineType(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.content()).isNotNull().hasSize(1).containsExactly(restaurant);

        // public access: não deve consultar role
        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should().findByCuisineType(query);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver restaurantes")
    void shouldReturnEmptyPageWhenNoRestaurantsFound() {
        // Arrange
        String cuisineType = "NonExistentCuisine";

        int pageNumber = 0;
        int pageSize = 10;
        long totalElements = 0L;

        PagedQuery<String> query = new PagedQuery<>(cuisineType, pageNumber, pageSize);

        Page<Restaurant> expectedPage = new Page<>(
                pageNumber,
                pageSize,
                totalElements,
                List.of()
        );

        given(restaurantGateway.findByCuisineType(query)).willReturn(expectedPage);

        // Act
        Page<Restaurant> result = useCase.execute(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.pageNumber()).isEqualTo(pageNumber);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero(); // totalElements=0 => totalPages=0
        assertThat(result.content()).isEmpty();

        // public access: não deve consultar role
        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).should().findByCuisineType(query);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando a consulta for nula (validação do UseCaseBase)")
    void shouldThrowExceptionWhenQueryIsNull() {
        // Act / Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query cannot be null.");

        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).shouldHaveNoInteractions();
    }
}
