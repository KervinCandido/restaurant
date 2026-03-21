package br.com.fiap.restaurant.restaurant.infra.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Dados para criação ou atualização de um restaurante")
public class RestaurantRequest {
    @Schema(description = "Nome do restaurante", example = "D.O.M.")
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Schema(description = "Tipo de cozinha do restaurante", example = "Brasileira")
    @NotBlank
    @Size(min = 4, max = 100)
    private String cuisineType;

    @Schema(description = "Endereço do restaurante")
    @Valid
    @NotNull
    private AddressRequest address;

    @Schema(description = "ID do proprietário do restaurante", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    @NotNull
    private UUID ownerId;

    @Schema(description = "Horários de funcionamento do restaurante (um para cada dia da semana)")
    @Valid
    @NotNull
    @Size(min = 1, max = 7)
    private List<OpeningHoursRequest> openingHours;

    @Schema(description = "Itens do cardápio do restaurante")
    @Valid
    private List<MenuItemRequest> menu;

    @Schema(description = "Lista de IDs dos funcionários do restaurante")
    private List<UUID> employees;
}
