package br.com.fiap.restaurant.restaurant.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RESTAURANT_EXCHANGE = "ex.restaurant";
    public static final String RESTAURANT_CREATE_QUEUE = "order.restaurant.created";
    public static final String RESTAURANT_UPDATE_QUEUE = "order.restaurant.updated";
    public static final String RESTAURANT_DELETE_QUEUE = "order.restaurant.deleted";

    public static final String RESTAURANT_CREATE_ROUTING_KEY = "restaurant.created";
    public static final String RESTAURANT_UPDATE_ROUTING_KEY = "restaurant.updated";
    public static final String RESTAURANT_DELETE_ROUTING_KEY = "restaurant.deleted";

    public static final String MENU_ITEM_CREATE_QUEUE = "order.menuitem.created";
    public static final String MENU_ITEM_UPDATE_QUEUE = "order.menuitem.updated";
    public static final String MENU_ITEM_DELETE_QUEUE = "order.menuitem.deleted";

    public static final String MENU_ITEM_CREATE_ROUTING_KEY = "menuitem.created";
    public static final String MENU_ITEM_UPDATE_ROUTING_KEY = "menuitem.updated";
    public static final String MENU_ITEM_DELETE_ROUTING_KEY = "menuitem.deleted";

    /* Consumer */
    public static final String RESTAURANT_USER_CREATE_QUEUE = "restaurant.user.created";
    public static final String RESTAURANT_USER_UPDATE_QUEUE = "restaurant.user.updated";
    public static final String RESTAURANT_USER_DELETE_QUEUE = "restaurant.user.deleted";

    @Bean("restaurantExchange")
    public DirectExchange restaurantExchange() {
        return new DirectExchange(RESTAURANT_EXCHANGE);
    }

    @Bean("restaurantCreateQueue")
    public Queue restaurantCreateQueue() {
        return QueueBuilder.durable(RESTAURANT_CREATE_QUEUE).quorum().build();
    }

    @Bean("restaurantUpdateQueue")
    public Queue restaurantUpdateQueue() {
        return QueueBuilder.durable(RESTAURANT_UPDATE_QUEUE).quorum().build();
    }

    @Bean("restaurantDeleteQueue")
    public Queue restaurantDeleteQueue() {
        return QueueBuilder.durable(RESTAURANT_DELETE_QUEUE).quorum().build();
    }

    @Bean("menuItemCreateQueue")
    public Queue menuItemCreateQueue() {
        return QueueBuilder.durable(MENU_ITEM_CREATE_QUEUE).quorum().build();
    }

    @Bean("menuItemUpdateQueue")
    public Queue menuItemUpdateQueue() {
        return QueueBuilder.durable(MENU_ITEM_UPDATE_QUEUE).quorum().build();
    }

    @Bean("menuItemDeleteQueue")
    public Queue menuItemDeleteQueue() {
        return QueueBuilder.durable(MENU_ITEM_DELETE_QUEUE).quorum().build();
    }

    @Bean("restaurantCreateBind")
    public Binding restaurantCreateBind(@Qualifier("restaurantCreateQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RESTAURANT_CREATE_ROUTING_KEY);
    }

    @Bean("restaurantUpdateBind")
    public Binding restaurantUpdateBind(@Qualifier("restaurantUpdateQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RESTAURANT_UPDATE_ROUTING_KEY);
    }

    @Bean("menuItemCreateBind")
    public Binding menuItemCreateBind(@Qualifier("menuItemCreateQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(MENU_ITEM_CREATE_ROUTING_KEY);
    }

    @Bean("menuItemUpdateBind")
    public Binding menuItemUpdateBind(@Qualifier("menuItemUpdateQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(MENU_ITEM_UPDATE_ROUTING_KEY);
    }

    @Bean("menuItemDeleteBind")
    public Binding menuItemDeleteBind(@Qualifier("menuItemDeleteQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(MENU_ITEM_DELETE_ROUTING_KEY);
    }

    @Bean("restaurantDeleteBind")
    public Binding restaurantDeleteBind(@Qualifier("restaurantDeleteQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RESTAURANT_DELETE_ROUTING_KEY);
    }


    /* Consumer */
    @Bean("restaurantUserCreateQueue")
    public Queue restaurantUserCreateQueue() {
        return QueueBuilder.durable(RESTAURANT_USER_CREATE_QUEUE).quorum().build();
    }

    @Bean("restaurantUserUpdateQueue")
    public Queue restaurantUserUpdateQueue() {
        return QueueBuilder.durable(RESTAURANT_USER_UPDATE_QUEUE).quorum().build();
    }

    @Bean("restaurantUserDeleteQueue")
    public Queue restaurantUserDeleteQueue() {
        return QueueBuilder.durable(RESTAURANT_USER_DELETE_QUEUE).quorum().build();
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
