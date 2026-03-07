package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.auth.LoggedUserGatewayAdapter;
import br.com.fiap.restaurant.restaurant.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.core.gateway.UserGateway;
import br.com.fiap.restaurant.restaurant.infra.persistence.adapter.RestaurantGatewayAdapter;
import br.com.fiap.restaurant.restaurant.infra.persistence.adapter.UserGatewayAdapter;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.RestaurantRepository;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreGatewayConfig {

    @Bean
    public LoggedUserGateway loggedUserGateway() {
        return new LoggedUserGatewayAdapter();
    }

    @Bean
    public RestaurantGateway restaurantGateway(RestaurantRepository restaurantRepository) {
        return new RestaurantGatewayAdapter(restaurantRepository);
    }

    @Bean
    public UserGateway userGateway(UserRepository userRepository) {
        return new UserGatewayAdapter(userRepository);
    }
}

