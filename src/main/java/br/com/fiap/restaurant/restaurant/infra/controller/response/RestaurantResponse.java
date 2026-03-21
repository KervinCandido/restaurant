package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados de resposta detalhados de um restaurante (visão de gestão)")
public record RestaurantResponse (
        @Schema(description = "ID único do restaurante", example = "1")
        Long id,
        @Schema(description = "Nome do restaurante", example = "D.O.M.")
        String name,
        @Schema(description = "Tipo de cozinha", example = "Brasileira")
        String cuisineType,
        @Schema(description = "Endereço do restaurante")
        AddressResponse address,
        @Schema(description = "ID do proprietário do restaurante", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        UUID owner,
        @Schema(description = "Lista de horários de funcionamento")
        List<OpeningHoursResponse> openingHours,
        @Schema(description = "Lista de itens do cardápio")
        List<MenuItemResponse> menu,
        @Schema(description = "Lista de IDs dos funcionários")
        List<UUID> employees
){}
