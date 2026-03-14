package br.com.fiap.restaurant.restaurant.infra.consumer;

import br.com.fiap.restaurant.restaurant.core.controller.UserController;
import br.com.fiap.restaurant.restaurant.core.inbound.UserInput;
import br.com.fiap.restaurant.restaurant.infra.config.RabbitMQConfig;
import br.com.fiap.restaurant.restaurant.infra.consumer.dto.EventDTO;
import br.com.fiap.restaurant.restaurant.infra.consumer.dto.UserDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

    private final UserController userController;

    public UserConsumer(UserController userController) {
        this.userController = Objects.requireNonNull(userController, "UserController cannot be null.");
    }

    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_USER_CREATE_QUEUE)
    public void createUserConsumer(EventDTO<UserDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws java.io.IOException {
        try {
            log.info("Consumindo CreateUserEvent: {}", eventDTO);
            var userDTO = eventDTO.body();
            UserInput userInput = new UserInput(userDTO.uuid(), userDTO.roles());
            userController.createUser(userInput);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_USER_UPDATE_QUEUE)
    public void updateUserConsumer(EventDTO<UserDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws java.io.IOException {
        try {
            log.info("Consumindo UpdateUserEvent: {}", eventDTO);
            var userDTO = eventDTO.body();
            UserInput userInput = new UserInput(userDTO.uuid(), userDTO.roles());
            userController.updateUser(userInput);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_USER_DELETE_QUEUE)
    public void deleteUserConsume(EventDTO<UserDTO> eventDTO, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws java.io.IOException {
        try {
            log.info("Consumindo DeleteUserEvent: {}", eventDTO);
            var userDTO = eventDTO.body();
            userController.deleteUser(userDTO.uuid());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }

}
