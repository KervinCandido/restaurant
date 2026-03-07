package br.com.fiap.restaurant.restaurant.infra.publisher;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.infra.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
public class RestaurantCreatedPublisher implements PublisherGateway<Restaurant> {

    private final RabbitTemplate rabbitTemplate;

    public RestaurantCreatedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Future<Void> publish(Restaurant restaurant) {
        return CompletableFuture.runAsync(() -> rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, restaurant));
    }
}
