package br.com.fiap.restaurant.restaurant.core.usecase.restaurant;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.inbound.CreateRestaurantInput;
import br.com.fiap.restaurant.restaurant.core.inbound.UpdateRestaurantInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para RestaurantUseCaseFacade")
class RestaurantUseCaseFacadeTest {

    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;
    @Mock
    private UpdateRestaurantUseCase updateRestaurantUseCase;
    @Mock
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    @Mock
    private ListRestaurantsUseCase listRestaurantsUseCase;
    @Mock
    private DeleteRestaurantUseCase deleteRestaurantUseCase;
    @Mock
    private ListRestaurantsByCuisineTypeUseCase listRestaurantsByCuisineTypeUseCase;
    @Mock
    private GetRestaurantManagementByIdUseCase getRestaurantManagementByIdUseCase;
    @Mock
    private ListRestaurantsPagedUseCase listRestaurantsPagedUseCase;

    private RestaurantUseCaseFacade facade;

    @BeforeEach
    void setUp() {
        facade = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase)
                .build();
    }

    @Test
    @DisplayName("Deve criar restaurante com sucesso")
    void deveCriarRestauranteComSucesso() {
        // Given
        CreateRestaurantInput input = mock(CreateRestaurantInput.class);
        Restaurant restaurant = mock(Restaurant.class);
        when(createRestaurantUseCase.execute(input)).thenReturn(restaurant);

        // When
        Restaurant result = facade.createRestaurant(input);

        // Then
        assertThat(result).isEqualTo(restaurant);
        verify(createRestaurantUseCase).execute(input);
    }

    @Test
    @DisplayName("Deve atualizar restaurante com sucesso")
    void deveAtualizarRestauranteComSucesso() {
        // Given
        UpdateRestaurantInput input = mock(UpdateRestaurantInput.class);

        // When
        facade.updateRestaurant(input);

        // Then
        verify(updateRestaurantUseCase).execute(input);
    }

    @Test
    @DisplayName("Deve buscar restaurante por ID com sucesso")
    void deveBuscarRestaurantePorIdComSucesso() {
        // Given
        Long id = 1L;
        Restaurant restaurant = mock(Restaurant.class);
        when(getRestaurantByIdUseCase.execute(id)).thenReturn(Optional.of(restaurant));

        // When
        Optional<Restaurant> result = facade.findById(id);

        // Then
        assertThat(result).isPresent().contains(restaurant);
        verify(getRestaurantByIdUseCase).execute(id);
    }

    @Test
    @DisplayName("Deve buscar gerenciamento de restaurante por ID com sucesso")
    void deveBuscarGerenciamentoRestaurantePorIdComSucesso() {
        // Given
        Long id = 1L;
        Restaurant restaurant = mock(Restaurant.class);
        when(getRestaurantManagementByIdUseCase.execute(id)).thenReturn(Optional.of(restaurant));

        // When
        Optional<Restaurant> result = facade.findManagementById(id);

        // Then
        assertThat(result).isPresent().contains(restaurant);
        verify(getRestaurantManagementByIdUseCase).execute(id);
    }

    @Test
    @DisplayName("Deve listar todos os restaurantes com sucesso")
    void deveListarTodosRestaurantesComSucesso() {
        // Given
        Restaurant restaurant = mock(Restaurant.class);
        List<Restaurant> restaurants = List.of(restaurant);
        when(listRestaurantsUseCase.execute()).thenReturn(restaurants);

        // When
        List<Restaurant> result = facade.findAll();

        // Then
        assertThat(result).hasSize(1).contains(restaurant);
        verify(listRestaurantsUseCase).execute();
    }

    @Test
    @DisplayName("Deve listar todos os restaurantes paginados com sucesso")
    void deveListarTodosRestaurantesPaginadosComSucesso() {
        // Given
        PagedQuery<Void> query = new PagedQuery<>(null, 0, 10);
        Page<Restaurant> page = new Page<>(0, 10, 1, List.of());
        when(listRestaurantsPagedUseCase.execute(query)).thenReturn(page);

        // When
        Page<Restaurant> result = facade.findAll(query);

        // Then
        assertThat(result).isEqualTo(page);
        verify(listRestaurantsPagedUseCase).execute(query);
    }

    @Test
    @DisplayName("Deve buscar restaurantes por tipo de cozinha paginado com sucesso")
    void deveBuscarRestaurantesPorTipoCozinhaPaginadoComSucesso() {
        // Given
        PagedQuery<String> query = new PagedQuery<>("Italian", 0, 10);
        Page<Restaurant> page = new Page<>(0, 10, 1, List.of());
        when(listRestaurantsByCuisineTypeUseCase.execute(query)).thenReturn(page);

        // When
        Page<Restaurant> result = facade.findByCuisineType(query);

        // Then
        assertThat(result).isEqualTo(page);
        verify(listRestaurantsByCuisineTypeUseCase).execute(query);
    }

    @Test
    @DisplayName("Deve deletar restaurante por ID com sucesso")
    void deveDeletarRestaurantePorIdComSucesso() {
        // Given
        Long id = 1L;

        // When
        facade.deleteById(id);

        // Then
        verify(deleteRestaurantUseCase).execute(id);
    }

    @Test
    @DisplayName("Deve lançar exceção se CreateRestaurantUseCase for nulo no Builder")
    void deveLancarExcecaoSeCreateRestaurantUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                //.createRestaurantUseCase(null) // Missing
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se UpdateRestaurantUseCase for nulo no Builder")
    void deveLancarExcecaoSeUpdateRestaurantUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                //.updateRestaurantUseCase(null) // Missing
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("UpdateRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se GetRestaurantByIdUseCase for nulo no Builder")
    void deveLancarExcecaoSeGetRestaurantByIdUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                //.getRestaurantByIdUseCase(null) // Missing
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetByIdRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se ListRestaurantsUseCase for nulo no Builder")
    void deveLancarExcecaoSeListRestaurantsUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                //.listRestaurantsUseCase(null) // Missing
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetAllRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se DeleteRestaurantUseCase for nulo no Builder")
    void deveLancarExcecaoSeDeleteRestaurantUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                //.deleteRestaurantUseCase(null) // Missing
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DeleteRestaurantUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se ListRestaurantsByCuisineTypeUseCase for nulo no Builder")
    void deveLancarExcecaoSeListRestaurantsByCuisineTypeUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                //.listRestaurantsByCuisineTypeUseCase(null) // Missing
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase)
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ListRestaurantsByCuisineTypeUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se GetRestaurantManagementByIdUseCase for nulo no Builder")
    void deveLancarExcecaoSeGetRestaurantManagementByIdUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder builder = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                //.getRestaurantManagementByIdUseCase(null) // Missing
                .listRestaurantsPagedUseCase(listRestaurantsPagedUseCase);
        assertThatThrownBy(builder::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("GetRestaurantManagementByIdUseCase cannot be null.");
    }

    @Test
    @DisplayName("Deve lançar exceção se ListRestaurantsPagedUseCase for nulo no Builder")
    void deveLancarExcecaoSeListRestaurantsPagedUseCaseForNulo() {
        RestaurantUseCaseFacade.Builder restaurantManagementByIdUseCase = new RestaurantUseCaseFacade.Builder()
                .createRestaurantUseCase(createRestaurantUseCase)
                .updateRestaurantUseCase(updateRestaurantUseCase)
                .getRestaurantByIdUseCase(getRestaurantByIdUseCase)
                .listRestaurantsUseCase(listRestaurantsUseCase)
                .deleteRestaurantUseCase(deleteRestaurantUseCase)
                .listRestaurantsByCuisineTypeUseCase(listRestaurantsByCuisineTypeUseCase)
                //.listRestaurantsPagedUseCase(null) // Missing
                .getRestaurantManagementByIdUseCase(getRestaurantManagementByIdUseCase);
        assertThatThrownBy(restaurantManagementByIdUseCase::build)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ListRestaurantsPagedUseCase cannot be null.");
    }
}
