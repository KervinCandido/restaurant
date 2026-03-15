package br.com.fiap.restaurant.restaurant.infra.message.publisher;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.domain.User;
import br.com.fiap.restaurant.restaurant.core.domain.valueobject.Address;
import br.com.fiap.restaurant.restaurant.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.restaurant.infra.message.dto.MenuItemDTO;
import br.com.fiap.restaurant.restaurant.infra.message.dto.RestaurantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para RestaurantCreatedPublisher")
class RestaurantCreatedPublisherTest {

    private RestaurantCreatedPublisher publisher;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Captor
    private ArgumentCaptor<EventDTO<RestaurantDTO>> eventDTOArgumentCaptor;

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
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", address, "Italian", owner);
        MenuItem menuItem = new MenuItem(1L, "Pizza", "Delicious pizza", new BigDecimal("10.99"), false, "/pizza.jpg");
        restaurant.addMenuItem(menuItem);
        // When
        Future<Void> future = publisher.publish(restaurant);
        future.get(); // Wait for async execution

        // Then
        then(rabbitTemplate).should().convertAndSend (
                anyString(),
                anyString(),
                eventDTOArgumentCaptor.capture()
        );
        assertThat(future).isDone();

        EventDTO<RestaurantDTO> capturedEventDTO = eventDTOArgumentCaptor.getValue();
        assertThat(capturedEventDTO).isNotNull();
        assertThat(capturedEventDTO.uuid()).isNotNull();
        assertThat(capturedEventDTO.createTimeStamp()).isNotNull();
        assertThat(capturedEventDTO.body()).isNotNull();
        assertThat(capturedEventDTO.body().id()).isNotNull().isEqualTo(restaurant.getId());
        assertThat(capturedEventDTO.body().menu())
                .isNotNull()
                .hasSize(1)
                .containsExactlyInAnyOrder(new MenuItemDTO(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), menuItem.getRestaurantOnly()));
    }
}
