package br.com.fiap.restaurant.restaurant.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Dados de resposta resumidos de um restaurante (visão pública)")
public record RestaurantSummaryResponse (
    @Schema(description = "ID único do restaurante", example = "1")
    Long id,
    @Schema(description = "Nome do restaurante", example = "D.O.M.")
    String name,
    @Schema(description = "Tipo de cozinha", example = "Brasileira")
    String cuisineType,
    @Schema(description = "Endereço do restaurante")
    AddressResponse address,
    @Schema(description = "Lista de itens do cardápio")
    List<MenuItemResponse> menu,
    @Schema(description = "Lista de horários de funcionamento")
    List<OpeningHoursResponse> openingHours
) {}
