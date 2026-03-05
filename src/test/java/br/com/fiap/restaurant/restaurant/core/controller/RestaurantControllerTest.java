package br.com.fiap.restaurant.restaurant.core.controller;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.inbound.AddressInput;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.outbound.RestaurantManagementOutput;
import br.com.fiap.restaurant.restaurant.core.usecase.CreateRestaurantUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RestaurantController")
class RestaurantControllerTest {

    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    @InjectMocks
    private RestaurantController restaurantController;

    @DisplayName("Deve criar restaurante com sucesso")
    @Test
    void deveCriarRestauranteComSucesso() {
        String restaurantName = "Restaurante Teste";
        String cuisineType = "Italiana";
        AddressInput addressInput = new AddressInput("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        UUID ownerId = UUID.randomUUID();

        CreateRestaurantInput input = new CreateRestaurantInput(
                restaurantName,
                addressInput,
                cuisineType,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet(),
                ownerId
        );

        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        User owner = new User(ownerId, Set.of(User.RESTAURANT_OWNER));
        Restaurant restaurant = new Restaurant(1L, restaurantName, address, cuisineType, owner);

        given(createRestaurantUseCase.execute(any(CreateRestaurantInput.class))).willReturn(restaurant);

        RestaurantManagementOutput output = restaurantController.createRestaurant(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(restaurant.getId());
        assertThat(output.name()).isEqualTo(restaurant.getName());
        assertThat(output.cuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(output.ownerUuid()).isEqualTo(ownerId);

        then(createRestaurantUseCase).should().execute(input);
    }

    @DisplayName("Deve lançar NullPointerException se input for nulo")
    @Test
    void deveLancarNullPointerSeInputForNulo() {
        assertThatThrownBy(() -> restaurantController.createRestaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("createRestaurantInput cannot be null");

        then(createRestaurantUseCase).shouldHaveNoInteractions();
    }
}
