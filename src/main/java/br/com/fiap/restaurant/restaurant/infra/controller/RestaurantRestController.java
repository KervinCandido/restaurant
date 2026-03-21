package br.com.fiap.restaurant.restaurant.infra.controller;

import br.com.fiap.restaurant.restaurant.core.controller.RestaurantController;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.infra.controller.mapper.RestaurantRestMapper;
import br.com.fiap.restaurant.restaurant.infra.controller.request.RestaurantRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantResponse;
import br.com.fiap.restaurant.restaurant.infra.controller.response.RestaurantSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/restaurants")
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de restaurantes")
public class RestaurantRestController {

    private final RestaurantController restaurantController;
    private final RestaurantRestMapper restaurantRestMapper;

    public RestaurantRestController(RestaurantController restaurantController, RestaurantRestMapper restaurantRestMapper) {
        this.restaurantController = restaurantController;
        this.restaurantRestMapper = restaurantRestMapper;
    }

    @Operation(summary = "Criar restaurante", description = "Cria um novo restaurante no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a criação do restaurante.")
    })
    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest restaurantRequest, UriComponentsBuilder uriComponentsBuilder) {
        var restaurant = restaurantController.createRestaurant(restaurantRestMapper.toInput(restaurantRequest));

        var uri = uriComponentsBuilder.path("/restaurants/{id}").buildAndExpand(restaurant.id()).toUri();
        return ResponseEntity.created(uri).body(restaurantRestMapper.toResponse(restaurant));
    }

    @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurante atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a atualização."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody @Valid RestaurantRequest  input) {
        var restaurantInput = restaurantRestMapper.toUpdateInput(input, id);
        restaurantController.updateRestaurant(restaurantInput);
    }

    @Operation(summary = "Buscar restaurante por ID (Público)", description = "Busca um restaurante pelo ID e retorna suas informações públicas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantSummaryResponse> getPublicById(@PathVariable("id") Long id) {
        return restaurantController.findById(id).map(restaurantRestMapper::toResponseSummary).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar restaurante por ID (Gestão)", description = "Busca um restaurante pelo ID e retorna todas as suas informações para gestão.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    @GetMapping("/{id}/management")
    public ResponseEntity<RestaurantResponse> getManagementById(@PathVariable Long id) {
        return restaurantController.findManagementById(id).map(restaurantRestMapper::toResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar restaurantes", description = "Lista todos os restaurantes de forma paginada, com opção de filtro por tipo de cozinha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada com sucesso.")
    })
    @SecurityRequirements
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

    @Operation(summary = "Excluir restaurante", description = "Exclui um restaurante do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurante excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        restaurantController.deleteById(id);
    }
}
