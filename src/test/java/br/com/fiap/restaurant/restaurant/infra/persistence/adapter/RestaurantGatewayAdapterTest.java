package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.mapper.UserMapper;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.RestaurantRepository;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import br.com.fiap.restaurant.restaurant.utils.core.MenuItemBuilder;
import br.com.fiap.restaurant.restaurant.utils.core.OpeningHoursBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(RestaurantGatewayAdapter.class)
@DisplayName("Testes de Integração para RestaurantGatewayAdapter")
class RestaurantGatewayAdapterTest {

    @Autowired
    private RestaurantGatewayAdapter adapter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private User owner;
    private Address address;

    @BeforeEach
    void setUp() {
        owner = UserMapper.toDomain(userRepository
                .findById(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .orElseThrow(() -> new RuntimeException("User not found")));
        this.address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
    }

    @Test
    @DisplayName("Deve salvar restaurante com sucesso")
    void deveSalvarRestauranteBasicoComSucesso() {
        // Given
        String cuisineType = "Brazilian";
        String restaurantName = "Tasty Food";

        Restaurant restaurant = new Restaurant (
                null,
                restaurantName,
                this.address,
                cuisineType,
                owner
        );

        // When
        Restaurant savedRestaurant = adapter.save(restaurant);

        // Then
        assertThat(savedRestaurant).isNotNull();
        assertThat(savedRestaurant.getId()).isNotNull();
        assertThat(savedRestaurant.getName()).isEqualTo(restaurantName);
        assertThat(savedRestaurant.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(savedRestaurant.getAddress()).isNotNull().usingRecursiveComparison().isEqualTo(address);

        assertThat(restaurantRepository.findById(savedRestaurant.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve salvar restaurante com horarios e menu com sucesso")
    void deveSalvarRestauranteComHorariosEMenuComSucesso() {
        // Given
        String cuisineType = "Brazilian";
        String restaurantName = "Tasty Food";

        Restaurant restaurant = new Restaurant (
                null,
                restaurantName,
                this.address,
                cuisineType,
                owner
        );

        restaurant.addOpeningHours(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.THURSDAY).build());
        restaurant.addOpeningHours(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.FRIDAY).build());
        restaurant.addOpeningHours(OpeningHoursBuilder.builder().withoutId().withDayOfWeek(DayOfWeek.SATURDAY).build());

        restaurant.addMenuItem(MenuItemBuilder.builder().withoutId().withName("Feijoada").build());
        restaurant.addMenuItem(MenuItemBuilder.builder().withoutId().withName("Virado à paulista").build());
        restaurant.addMenuItem(MenuItemBuilder.builder().withoutId().withName("Picadinho de carne").build());
        restaurant.addMenuItem(MenuItemBuilder.builder().withoutId().withName("Leitão à pururuca").build());


        // When
        Restaurant savedRestaurant = adapter.save(restaurant);

        // Then
        assertThat(savedRestaurant).isNotNull();
        assertThat(savedRestaurant.getId()).isNotNull();
        assertThat(savedRestaurant.getName()).isEqualTo(restaurantName);
        assertThat(savedRestaurant.getCuisineType()).isNotNull().isEqualTo(cuisineType);
        assertThat(savedRestaurant.getAddress()).isNotNull().usingRecursiveComparison().isEqualTo(address);

        assertThat(restaurantRepository.findById(savedRestaurant.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve retornar true se existe restaurante com o nome")
    void deveRetornarTrueSeExisteRestauranteComNome() {
        // Given
        String restaurantName = "Existing Restaurant";
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName(restaurantName);
        restaurantEntity.setOwner(UserMapper.toEntity(owner));
        restaurantEntity.setCuisineType("Italian");
        restaurantRepository.save(restaurantEntity);

        // When
        boolean exists = adapter.existsRestaurantWithName(restaurantName);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false se não existe restaurante com o nome")
    void deveRetornarFalseSeNaoExisteRestauranteComNome() {
        // Given
        String restaurantName = "Non Existing Restaurant";

        // When
        boolean exists = adapter.existsRestaurantWithName(restaurantName);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar true se restaurante com nome existe excluindo ID específico")
    void shouldReturnTrueIfRestaurantWithNameExistsExcludingId() {
        // Arrange - Cria UM restaurante (não podemos criar duplicado por causa da unique constraint)
        Restaurant restaurant = new Restaurant(
                null,
                "Unique Restaurant Name",
                this.address,
                "Italian",
                owner
        );
        Restaurant saved = adapter.save(restaurant);
        Long existingId = saved.getId();

        // Caso 1: Exclui um ID DIFERENTE → deve encontrar o restaurante → true
        boolean existsWithDifferentId = adapter.existsRestaurantWithNameExcludingId("Unique Restaurant Name", 999L);

        // Caso 2: Exclui o PRÓPRIO ID → não deve considerar → false
        boolean existsWithOwnId = adapter.existsRestaurantWithNameExcludingId("Unique Restaurant Name", existingId);

        // Caso 3: Nome que NÃO existe → stream vazia → false
        boolean existsWithNonExistentName = adapter.existsRestaurantWithNameExcludingId("Non Existent Name", existingId);

        // Asserts - Cobertura completa do lambda
        assertThat(existsWithDifferentId)
                .as("Deve retornar true quando há match e o ID é diferente (branch !equals = true)")
                .isTrue();

        assertThat(existsWithOwnId)
                .as("Deve retornar false quando o único match é o próprio ID (branch !equals = false)")
                .isFalse();

        assertThat(existsWithNonExistentName)
                .as("Deve retornar false quando stream é vazia (anyMatch false)")
                .isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se restaurante com nome não existe excluindo ID")
    void shouldReturnFalseIfRestaurantWithNameDoesNotExistExcludingId() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Unique Name",
                this.address,
                "Italian",
                owner
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // When
        boolean exists = adapter.existsRestaurantWithNameExcludingId("Non Existent Name", id);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false se nome existe mas pertence ao mesmo ID excluído")
    void shouldReturnFalseIfNameExistsButBelongsToExcludedId() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Same Name",
                this.address,
                "Italian",
                owner
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // When
        boolean exists = adapter.existsRestaurantWithNameExcludingId("Same Name", id);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar todos os restaurantes")
    void shouldReturnAllRestaurants() {
        // Given
        Restaurant restaurant1 = new Restaurant(
                null,
                "Restaurant 1",
                this.address,
                "Italian",
                owner
        );
        Restaurant restaurant2 = new Restaurant(
                null,
                "Restaurant 2",
                this.address,
                "Japanese",
                owner
        );
        adapter.save(restaurant1);
        adapter.save(restaurant2);

        // When
        List<Restaurant> restaurants = adapter.findAll();

        // Then
        assertThat(restaurants).hasSizeGreaterThanOrEqualTo(2);
        assertThat(restaurants).extracting(Restaurant::getName)
                .contains("Restaurant 1", "Restaurant 2");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver restaurantes")
    void shouldReturnEmptyListWhenNoRestaurants() {
        // Given
        restaurantRepository.deleteAll();

        // When
        List<Restaurant> restaurants = adapter.findAll();

        // Then
        assertThat(restaurants).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar página de restaurantes paginados")
    void shouldReturnPagedRestaurants() {
        // Given
        Restaurant restaurant1 = new Restaurant(
                null,
                "Restaurant A",
                this.address,
                "Italian",
                owner
        );
        Restaurant restaurant2 = new Restaurant(
                null,
                "Restaurant B",
                this.address,
                "Japanese",
                owner
        );
        Restaurant restaurant3 = new Restaurant(
                null,
                "Restaurant C",
                this.address,
                "Mexican",
                owner
        );
        adapter.save(restaurant1);
        adapter.save(restaurant2);
        adapter.save(restaurant3);

        PagedQuery<Void> query = new PagedQuery<>(null, 0, 2);

        // When
        Page<Restaurant> result = adapter.findAll(query);

        // Then
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(3L);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.pageNumber()).isZero();
    }

    @Test
    @DisplayName("Deve deletar restaurante com sucesso")
    void shouldDeleteRestaurantSuccessfully() {
        // Given
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant to Delete",
                this.address,
                "Mexican",
                owner
        );
        Restaurant savedRestaurant = adapter.save(restaurant);
        Long id = savedRestaurant.getId();

        // When
        adapter.delete(id);

        // Then
        assertThat(restaurantRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Deve não lançar exceção ao deletar restaurante inexistente")
    void shouldNotThrowExceptionWhenDeletingNonExistentRestaurant() {
        // Given
        Long nonExistentId = 999L;

        // When
        adapter.delete(nonExistentId);

        // Then
        assertThat(restaurantRepository.findById(nonExistentId)).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar restaurantes por tipo de cozinha")
    void shouldFindRestaurantsByCuisineType() {
        // Given
        adapter.save(new Restaurant(null, "Italian Place", this.address, "Italian", owner));
        adapter.save(new Restaurant(null, "Super Italian", this.address, "Italian", owner));
        adapter.save(new Restaurant(null, "Japanese Place", this.address, "Japanese", owner));

        PagedQuery<String> query = new PagedQuery<>("Italian", 0, 10);

        // When
        Page<Restaurant> result = adapter.findByCuisineType(query);

        // Then
        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).extracting(Restaurant::getCuisineType).containsOnly("Italian");
    }

    @Test
    @DisplayName("Deve retornar página vazia se nenhum restaurante corresponder ao tipo de cozinha")
    void shouldReturnEmptyPageWhenNoRestaurantMatchesCuisineType() {
        // Given
        adapter.save(new Restaurant(null, "Japanese Place", this.address, "Japanese", owner));
        PagedQuery<String> query = new PagedQuery<>("Mexican", 0, 10);

        // When
        Page<Restaurant> result = adapter.findByCuisineType(query);

        // Then
        assertThat(result.content()).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar restaurantes por tipo de cozinha ignorando case")
    void shouldFindRestaurantsByCuisineTypeIgnoringCase() {
        // Given
        adapter.save(new Restaurant(null, "Italian Place", this.address, "Italian", owner));
        PagedQuery<String> query = new PagedQuery<>("italian", 0, 10);

        // When
        Page<Restaurant> result = adapter.findByCuisineType(query);

        // Then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().getCuisineType()).isEqualTo("Italian");
    }

    @Test
    @DisplayName("Deve paginar os resultados corretamente")
    void shouldPaginateResultsCorrectly() {
        // Given
        adapter.save(new Restaurant(null, "Italian 1", this.address, "Italian", owner));
        adapter.save(new Restaurant(null, "Italian 2", this.address, "Italian", owner));
        adapter.save(new Restaurant(null, "Italian 3", this.address, "Italian", owner));

        PagedQuery<String> firstPageQuery = new PagedQuery<>("Italian", 0, 2);
        PagedQuery<String> secondPageQuery = new PagedQuery<>("Italian", 1, 2);

        // When
        Page<Restaurant> firstPage = adapter.findByCuisineType(firstPageQuery);
        Page<Restaurant> secondPage = adapter.findByCuisineType(secondPageQuery);

        // Then
        assertThat(firstPage.content()).hasSize(2);
        assertThat(firstPage.totalElements()).isEqualTo(3L);
        assertThat(firstPage.totalPages()).isEqualTo(2);
        assertThat(firstPage.pageNumber()).isZero();

        assertThat(secondPage.content()).hasSize(1);
        assertThat(secondPage.totalElements()).isEqualTo(3L);
        assertThat(secondPage.totalPages()).isEqualTo(2);
        assertThat(secondPage.pageNumber()).isOne();
    }

    @Test
    @DisplayName("Deve encontrar restaurante por ID")
    void shouldFindById() {
        // Arrange
        Restaurant restaurant = new Restaurant(
                null,
                "Test Restaurant",
                this.address,
                "Test Cuisine",
                owner
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // Act
        Optional<Restaurant> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getName()).isEqualTo("Test Restaurant");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void shouldReturnEmptyWhenFindByNonExistentId() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<Restaurant> result = adapter.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar restaurante por ID com dados de management")
    void shouldFindByIdWithManagement() {
        // Arrange
        Restaurant restaurant = new Restaurant(
                null,
                "Management Test",
                this.address,
                "Test Cuisine",
                owner
        );
        Restaurant saved = adapter.save(restaurant);
        Long id = saved.getId();

        // Act
        Optional<Restaurant> result = adapter.findByIdWithManagement(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getName()).isEqualTo("Management Test");
        // Verificar se dados de management estão presentes (ex: ownerId carregado)
        assertThat(result.get().getOwner()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente com management")
    void shouldReturnEmptyWhenFindByNonExistentIdWithManagement() {
        // Arrange
        Long nonExistentId = 999L;

        // Act
        Optional<Restaurant> result = adapter.findByIdWithManagement(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar restaurante sem funcionários quando employees estiver vazio")
    void shouldSaveRestaurantWithEmptyEmployees() {
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant No Employees",
                this.address,
                "Italian",
                owner
        );

        Restaurant saved = adapter.save(restaurant);

        RestaurantEntity entity = restaurantRepository.findById(saved.getId()).orElseThrow();
        assertThat(entity.getEmployees()).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar restaurante quando employees for null")
    void shouldSaveRestaurantWhenEmployeesIsNull() {
        Restaurant restaurant = new Restaurant(
                null,
                "Restaurant Null Employees",
                this.address,
                "Italian",
                owner
        );


        Restaurant saved = adapter.save(restaurant);

        assertThat(saved).isNotNull();
    }
}
