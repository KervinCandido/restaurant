package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
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
@DisplayName("Testes para ListRestaurantsUseCase")
class ListRestaurantsUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private ListRestaurantsUseCase useCase;

    @Test
    @DisplayName("Deve retornar lista de restaurantes quando usuário tiver permissão")
    void shouldReturnRestaurantsWhenUserHasPermission() {
        User owner = new User(UUID.randomUUID(), Set.of(User.RESTAURANT_OWNER));
        var address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        Restaurant r1 = new Restaurant(1L, "Restaurant I", address, "Italiana", owner);
        Restaurant r2 = new Restaurant(2L, "Restaurant II", address, "Italiana", owner);

        given(restaurantGateway.findAll()).willReturn(List.of(r1, r2));

        List<Restaurant> result = useCase.execute();

        assertThat(result).isNotNull().hasSize(2).containsExactly(r1, r2);

        then(restaurantGateway).should().findAll();
    }
}
