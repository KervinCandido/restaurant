package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
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
}
