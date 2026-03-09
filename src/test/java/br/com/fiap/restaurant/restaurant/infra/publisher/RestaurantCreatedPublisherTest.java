package br.com.fiap.restaurant.restaurant.infra.publisher;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para RestaurantCreatedPublisher")
class RestaurantCreatedPublisherTest {

    private RestaurantCreatedPublisher publisher;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @BeforeEach
    void setUp() {
        publisher = new RestaurantCreatedPublisher(rabbitTemplate);
    }

    @Test
    @DisplayName("Deve publicar restaurante com sucesso")
    void devePublicarRestauranteComSucesso() throws ExecutionException, InterruptedException {
        // Given
        Address address = new Address("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "CEP Teste");
        User owner = new User(UUID.randomUUID(), Set.of("RESTAURANT_OWNER"));
        Restaurant restaurant = new Restaurant(null, "Test Restaurant", address, "Italian", owner);

        // When
        Future<Void> future = publisher.publish(restaurant);
        future.get(); // Wait for async execution

        // Then
        verify(rabbitTemplate).convertAndSend(
                "restaurant-exchange",
                "novo-restaurant",
                restaurant
        );
        assertThat(future).isDone();
    }
}
