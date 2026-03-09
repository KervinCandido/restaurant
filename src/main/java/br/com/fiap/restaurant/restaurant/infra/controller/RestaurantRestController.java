package br.com.fiap.restaurant.restaurant.infra.controller;

import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.infra.controller.mapper.RestaurantRestMapper;
import br.com.fiap.restaurant.restaurant.infra.controller.request.RestaurantRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/restaurants")
public class RestaurantRestController {

    private final RestaurantController restaurantController;
    private final RestaurantRestMapper restaurantRestMapper;

    public RestaurantRestController(RestaurantController restaurantController, RestaurantRestMapper restaurantRestMapper) {
        this.restaurantController = restaurantController;
        this.restaurantRestMapper = restaurantRestMapper;
    }

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest, UriComponentsBuilder uriComponentsBuilder) {
        var restaurant = restaurantController.createRestaurant(restaurantRestMapper.toInput(restaurantRequest));

        var uri = uriComponentsBuilder.path("/restaurants/{id}").buildAndExpand(restaurant.id()).toUri();
        return ResponseEntity.created(uri).body(restaurantRestMapper.toResponse(restaurant));
    }
}
