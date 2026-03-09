package br.com.fiap.restaurant.restaurant.infra.controller;

import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.infra.controller.mapper.RestaurantRestMapper;
import br.com.fiap.restaurant.restaurant.infra.controller.request.RestaurantRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantResponse;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody @Valid RestaurantRequest  input) {
        var restaurantInput = restaurantRestMapper.toUpdateInput(input, id);
        restaurantController.updateRestaurant(restaurantInput);
    }

    // Público
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantSummaryResponse> getPublicById(@PathVariable("id") Long id) {
        return restaurantController.findById(id).map(restaurantRestMapper::toResponseSummary).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Gestão
    @GetMapping("/{id}/management")
    public ResponseEntity<RestaurantResponse> getManagementById(@PathVariable Long id) {
        return restaurantController.findManagementById(id).map(restaurantRestMapper::toResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<RestaurantSummaryResponse> listPaged(
            @RequestParam(required = false) String cuisineType,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (cuisineType != null && !cuisineType.isBlank()) {
            return restaurantController.findByCuisineType(cuisineType, pageNumber, pageSize).mapItems(restaurantRestMapper::toResponseSummary);
        }
        return restaurantController.findAll(pageNumber, pageSize).mapItems(restaurantRestMapper::toResponseSummary);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        restaurantController.deleteById(id);
    }
}
