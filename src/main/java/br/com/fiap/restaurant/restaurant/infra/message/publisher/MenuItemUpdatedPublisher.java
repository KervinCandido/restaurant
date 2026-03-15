package br.com.fiap.restaurant.restaurant.infra.message.publisher;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.restaurant.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.restaurant.infra.message.dto.MenuItemDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class MenuItemUpdatedPublisher implements PublisherGateway<MenuItem> {

    public static final String MENU_ITEM_UPDATE_EVENT_TYPE = "menuitem.update";
    private final RabbitTemplate rabbitTemplate;

    public MenuItemUpdatedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public CompletableFuture<Void> publish(MenuItem menuItem) {
        var eventDTO = new EventDTO<>(MENU_ITEM_UPDATE_EVENT_TYPE, new MenuItemDTO(menuItem));
        return CompletableFuture.runAsync(() ->
                rabbitTemplate.convertAndSend(RabbitMQConfig.RESTAURANT_EXCHANGE,
                        RabbitMQConfig.MENU_ITEM_UPDATE_ROUTING_KEY, eventDTO));
    }
}
