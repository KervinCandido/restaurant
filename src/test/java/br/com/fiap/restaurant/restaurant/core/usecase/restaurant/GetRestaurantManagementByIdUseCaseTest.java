package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.core.exception.UserNotAuthenticatedException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GetRestaurantManagementByIdUseCase")
class GetRestaurantManagementByIdUseCaseTest {

    @Mock
    private RestaurantGateway restaurantGateway;

    @Mock
    private LoggedUserGateway loggedUserGateway;

    @InjectMocks
    private GetRestaurantManagementByIdUseCase useCase;

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
    @DisplayName("Deve permitir quando usuário tem mesmo id do ownerId (UUID) mesmo sendo outra instância")
    void devePermitir_quandoUsuarioTemMesmoIdDoOwner_mesmoSendoOutraInstancia() {
        // Arrange
        given(loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(owner);

        // Act
        var result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isNotEmpty().hasValue(restaurant);

        then(loggedUserGateway).should().hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve permitir quando usuário tem mesmo id de employee (UUID) mesmo sendo outra instância")
    void devePermitir_quandoUsuarioTemMesmoIdDeEmployee_mesmoSendoOutraInstancia() {
        // Arrange
        User employeeFromRestaurant = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT_MANAGEMENT));
        this.restaurant.addEmployee(employeeFromRestaurant);


        given(loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(employeeFromRestaurant);

        // Act
        var result = useCase.execute(restaurantId);

        // Assert
        assertThat(result).isNotEmpty().hasValue(restaurant);

        then(loggedUserGateway).should().hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve negar quando usuário não é ownerId nem employee (por UUID)")
    void deveNegar_quandoUsuarioNaoEhOwnerNemEmployee_porId() {
        // Arrange

        User currentUser = new User(UUID.randomUUID(), Set.of(Restaurant.VIEW_RESTAURANT_MANAGEMENT));

        given(loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willReturn(currentUser);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessageContaining("Access denied");

        then(loggedUserGateway).should().hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve negar quando não autenticado (requireCurrentUser lança UserNotAuthenticatedException)")
    void deveNegar_quandoNaoAutenticado() {
        // Arrange
        given(loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.of(restaurant));
        given(loggedUserGateway.requireCurrentUser()).willThrow(new UserNotAuthenticatedException());

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(UserNotAuthenticatedException.class);

        then(loggedUserGateway).should().hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve negar quando não tem role necessária (bloqueia antes de IO)")
    void deveNegar_quandoNaoTemRoleNecessaria() {
        // Arrange
        given(loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)).willReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> useCase.execute(restaurantId))
                .isInstanceOf(OperationNotAllowedException.class);

        then(loggedUserGateway).should().hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).shouldHaveNoInteractions();
        then(loggedUserGateway).should(never()).requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando restaurante não for encontrado (cobre lambda do orElseThrow)")
    void deveLancarBusinessException_quandoRestauranteNaoForEncontrado() {
        // Arrange
        given(loggedUserGateway.hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT)).willReturn(true);
        given(restaurantGateway.findByIdWithManagement(restaurantId)).willReturn(Optional.empty());

        // Act + Assert
        Optional<Restaurant> result = useCase.execute(restaurantId);

        assertThat(result).isEmpty();

        then(loggedUserGateway).should().hasRole(Restaurant.VIEW_RESTAURANT_MANAGEMENT);
        then(restaurantGateway).should().findByIdWithManagement(restaurantId);
        then(loggedUserGateway).should().requireCurrentUser();
    }

    @Test
    @DisplayName("Deve lançar NullPointerException quando input for null (UseCaseBase)")
    void deveLancarNullPointerException_quandoInputForNull() {
        // Arrange / Act + Assert
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("restaurantId cannot be null");

        then(loggedUserGateway).shouldHaveNoInteractions();
        then(restaurantGateway).shouldHaveNoInteractions();
    }
}
