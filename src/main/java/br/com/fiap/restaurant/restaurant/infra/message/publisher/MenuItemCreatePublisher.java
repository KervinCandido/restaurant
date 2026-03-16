package br.com.fiap.restaurant.restaurant.infra.message.publisher;

import br.com.fiap.restaurant.restaurant.core.event.MenuItemEvent;
import br.com.fiap.restaurant.restaurant.core.gateway.PublisherGateway;
import br.com.fiap.restaurant.restaurant.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.restaurant.infra.message.dto.EventDTO;
import br.com.fiap.restaurant.restaurant.infra.message.dto.MenuItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class MenuItemCreatePublisher implements PublisherGateway<MenuItemEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemCreatePublisher.class);

    public static final String MENU_ITEM_CREATE_EVENT_TYPE = "menuitem.create";
    private final RabbitTemplate rabbitTemplate;

    public MenuItemCreatePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public CompletableFuture<Void> publish(MenuItemEvent menuItemEvent) {
        var eventDTO = new EventDTO<>(MENU_ITEM_CREATE_EVENT_TYPE, new MenuItemDTO(menuItemEvent));
        logger.info("Publishing menu item create event: {}", eventDTO);
        return CompletableFuture.runAsync(() ->
                rabbitTemplate.convertAndSend(RabbitMQConfig.RESTAURANT_EXCHANGE,
                        RabbitMQConfig.MENU_ITEM_CREATE_ROUTING_KEY, eventDTO));
    }
}
