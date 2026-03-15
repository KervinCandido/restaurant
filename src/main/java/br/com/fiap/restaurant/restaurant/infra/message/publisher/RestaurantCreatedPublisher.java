package br.com.fiap.restaurant.restaurant.infra.message.publisher;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.restaurant.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.restaurant.infra.message.dto.RestaurantDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class RestaurantCreatedPublisher implements PublisherGateway<Restaurant> {

    public static final String RESTAURANT_CREATE_EVENT_TYPE = "restaurant.create";
    private final RabbitTemplate rabbitTemplate;

    public RestaurantCreatedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public CompletableFuture<Void> publish(Restaurant restaurant) {
        var eventDTO = new EventDTO<>(RESTAURANT_CREATE_EVENT_TYPE, new RestaurantDTO(restaurant));
        return CompletableFuture.runAsync(() ->
                rabbitTemplate.convertAndSend(RabbitMQConfig.RESTAURANT_EXCHANGE,
                        RabbitMQConfig.RESTAURANT_CREATE_ROUTING_KEY, eventDTO));
    }
}
