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
    public static final String RESTAURANT_CREATE_QUEUE = "restaurant.restaurant.create";
    public static final String RESTAURANT_CREATE_ROUTING_KEY = "restaurant.create";

    /* Consumer */
    public static final String RESTAURANT_USER_CREATE_QUEUE = "restaurant.user.create";
    public static final String RESTAURANT_USER_UPDATE_QUEUE = "restaurant.user.update";
    public static final String RESTAURANT_USER_DELETE_QUEUE = "restaurant.user.delete";

    @Bean("restaurantExchange")
    public DirectExchange restaurantExchange() {
        return new DirectExchange(RESTAURANT_EXCHANGE);
    }

    @Bean("restaurantCreateQueue")
    public Queue restaurantCreateQueue() {
        return QueueBuilder.durable(RESTAURANT_CREATE_QUEUE).quorum().build();
    }

    @Bean("restauratCreateBind")
    public Binding restauratCreateBind(@Qualifier("restaurantCreateQueue") Queue queue, @Qualifier("restaurantExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RESTAURANT_CREATE_ROUTING_KEY);
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
