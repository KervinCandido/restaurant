package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetRestaurantByIdUseCase")
class GetRestaurantByIdUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

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
    @DisplayName("Deve retornar restaurante quando existir e usuário tiver permissão")
    void shouldReturnRestaurantWhenExistsAndUserHasPermission() {
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));

        var result = getRestaurantByIdUseCase.execute(restaurantId);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(restaurantId);

        then(restaurantGateway).should().findById(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existir")
    void shouldThrowBusinessExceptionWhenRestaurantNotFound() {
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        Optional<Restaurant> result = getRestaurantByIdUseCase.execute(restaurantId);

        assertThat(result).isEmpty();

        then(restaurantGateway).should().findById(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowNullPointerExceptionWhenInputIsNull() {
        assertThatThrownBy(() -> getRestaurantByIdUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("restaurantId cannot be null");

        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
