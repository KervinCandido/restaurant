package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.Restaurant;
import br.com.fiap.restaurant.restaurant.core.gateway.RestaurantGateway;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.mapper.RestaurantMapper;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.RestaurantRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RestaurantGatewayAdapter implements RestaurantGateway {

    private final RestaurantRepository restaurantRepository;

    public RestaurantGatewayAdapter(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public boolean existsRestaurantWithName(String restaurantName) {
        var restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName(restaurantName.strip());

        var matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher
                .GenericPropertyMatchers
                .exact()
                .ignoreCase());

        return restaurantRepository.exists(Example.of(restaurantEntity, matcher));
    }

    @Override
    @Transactional
    public Restaurant save(Restaurant restaurant) {
        var restaurantEntity = RestaurantMapper.toEntity(restaurant);

        var savedRestaurantEntity = restaurantRepository.save(restaurantEntity);

        return RestaurantMapper.toDomain(savedRestaurantEntity);
    }
}
