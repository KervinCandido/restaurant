package br.com.fiap.restaurant.restaurant.infra.controller;

import br.com.fiap.restaurant.restaurant.core.controller.MenuItemController;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.infra.controller.mapper.MenuItemRestMapper;
import br.com.fiap.restaurant.restaurant.infra.controller.request.MenuItemRequest;
import br.com.fiap.restaurant.restaurant.infra.controller.response.MenuItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/restaurants/{restaurant-id}/menu")
@Tag(name = "Cardápio", description = "Endpoints para gerenciamento do cardápio de um restaurante")
public class MenuRestController {

    private final MenuItemController controller;
    private final MenuItemRestMapper menuItemRestMapper;

    public MenuRestController(MenuItemController controller, MenuItemRestMapper menuItemRestMapper) {
        this.controller = controller;
        this.menuItemRestMapper = menuItemRestMapper;
    }

    @Operation(summary = "Listar itens do cardápio", description = "Lista todos os itens do cardápio de um restaurante de forma paginada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Itens do cardápio listados com sucesso.")
    })
    @SecurityRequirements
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MenuItemResponse> findAll (
            @Parameter(description = "ID do restaurante") @PathVariable("restaurant-id") Long restaurantId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return controller.findByRestaurant(restaurantId, pageNumber, pageSize).mapItems(menuItemRestMapper::toResponse);
    }

    @Operation(summary = "Adicionar item ao cardápio", description = "Adiciona um novo item ao cardápio do restaurante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para o item.")
    })
    @PostMapping
    public ResponseEntity<MenuItemResponse> addInMenu (
            @Parameter(description = "ID do restaurante") @PathVariable("restaurant-id") Long restaurantId,
            @RequestBody @Valid MenuItemRequest menuItemRequest,
            UriComponentsBuilder uriComponentsBuilder) {
        var createMenuItemInput = menuItemRestMapper.toCreateInput(menuItemRequest, restaurantId);
        var menuItemOutput = controller.addItemInMenu(createMenuItemInput);
        var uri = uriComponentsBuilder.path("/restaurants/{restaurant-id}/menu/{menu-id}")
                .buildAndExpand(restaurantId, menuItemOutput.id())
                .toUri();
        return ResponseEntity.created(uri).body(menuItemRestMapper.toResponse(menuItemOutput));
    }

    @Operation(summary = "Atualizar item do cardápio", description = "Atualiza um item existente no cardápio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a atualização."),
            @ApiResponse(responseCode = "404", description = "Item não encontrado.")
    })
    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateItem(
            @Parameter(description = "ID do restaurante (não utilizado na lógica, mas parte da URL)") @PathVariable("restaurant-id") Long restaurantId,
            @Parameter(description = "ID do item do cardápio") @PathVariable("id") Long id,
            @RequestBody @Valid MenuItemRequest menuItemRequest) {
        var updateInput = menuItemRestMapper.toUpdateInput(menuItemRequest, id);
        controller.updateMenuItem(updateInput);
    }

    @Operation(summary = "Excluir item do cardápio", description = "Exclui um item do cardápio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Item não encontrado.")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteById(
            @Parameter(description = "ID do restaurante (não utilizado na lógica, mas parte da URL)") @PathVariable("restaurant-id") Long restaurantId,
            @Parameter(description = "ID do item do cardápio") @PathVariable("id") Long id) {
        controller.deleteById(id);
    }

    @Operation(summary = "Buscar item por ID", description = "Busca um item específico do cardápio pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado."),
            @ApiResponse(responseCode = "404", description = "Item não encontrado.")
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getById(
            @Parameter(description = "ID do restaurante (não utilizado na lógica, mas parte da URL)") @PathVariable("restaurant-id") Long restaurantId,
            @Parameter(description = "ID do item do cardápio") @PathVariable("id") Long id) {
        return controller.getById(id).map(menuItemRestMapper::toResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
