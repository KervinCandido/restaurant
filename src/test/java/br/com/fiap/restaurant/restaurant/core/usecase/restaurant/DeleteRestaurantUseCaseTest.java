package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para DeleteRestaurantUseCase")
class DeleteRestaurantUseCaseTest {

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

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
    @DisplayName("Deve excluir restaurante com sucesso quando usuário logado é o DONO e tem role")
    void shouldDeleteRestaurantSuccessfullyWhenCurrentUserIsOwner() {
        given(loggedUserGateway.hasRole(Restaurant.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);

        deleteRestaurantUseCase.execute(restaurantId);

        then(loggedUserGateway).should().hasRole(Restaurant.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve excluir restaurante com sucesso quando usuário logado é FUNCIONÁRIO e tem role")
    void shouldDeleteRestaurantSuccessfullyWhenCurrentUserIsEmployee() {
        User employee = new User(UUID.randomUUID(), Set.of(Restaurant.DELETE_RESTAURANT));
        restaurant.addEmployee(employee);

        given(loggedUserGateway.hasRole(Restaurant.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(employee);

        deleteRestaurantUseCase.execute(restaurantId);

        then(loggedUserGateway).should().hasRole(Restaurant.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should().delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando tem role mas não é dono nem funcionário")
    void shouldThrowWhenCurrentUserIsNotOwnerNorEmployee() {
        User stranger = new User(UUID.randomUUID(), Set.of(Restaurant.DELETE_RESTAURANT));

        given(loggedUserGateway.hasRole(Restaurant.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(stranger);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(Restaurant.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
        then(restaurantGateway).should(never()).delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar OperationNotAllowedException quando usuário não tem role (não consulta findById)")
    void shouldThrowWhenUserHasNoRole() {
        given(loggedUserGateway.hasRole(Restaurant.DELETE_RESTAURANT)).willReturn(false);

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("The current user does not have permission");

        then(loggedUserGateway).should().hasRole(Restaurant.DELETE_RESTAURANT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não existe (não chama requireCurrentUser)")
    void shouldThrowWhenRestaurantNotFound() {
        given(loggedUserGateway.hasRole(Restaurant.DELETE_RESTAURANT)).willReturn(true);
        given(restaurantGateway.findById(restaurantId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(restaurantId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Restaurant not found");

        then(loggedUserGateway).should().hasRole(Restaurant.DELETE_RESTAURANT);
        then(restaurantGateway).should().findById(restaurantId);
        then(loggedUserGateway).should(never()).requireCurrentUser();
        then(restaurantGateway).should(never()).delete(restaurantId);
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input é nulo (UseCaseBase)")
    void shouldThrowWhenInputIsNull() {
        assertThatThrownBy(() -> deleteRestaurantUseCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("restaurantId cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
